package com.android.feedme.model.data

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.util.Locale

/**
 * Repository class for managing Recipe data.
 *
 * This class provides methods for adding, fetching, and updating Recipe objects in Firestore. It
 * also handles serialization and deserialization of Recipe objects to and from Firestore.
 *
 * @property db The Firestore database instance.
 */
class RecipeRepository(private val db: FirebaseFirestore) {

  private val ingredientsRepository = IngredientsRepository(db)
  val collectionPath = "recipesFinal"

  companion object {
    // Placeholder for the singleton instance
    lateinit var instance: RecipeRepository
      private set

    // Initialization method to be called once, e.g., in your Application class
    fun initialize(db: FirebaseFirestore) {
      instance = RecipeRepository(db)
    }
  }

  /**
   * Adds a recipe to the Firestore database.
   *
   * This method serializes the Recipe object into a map, replacing complex objects like Ingredient
   * with their IDs, and then stores it in Firestore under the recipes collection.
   *
   * @param recipe The Recipe object to be added to Firestore.
   * @param onSuccess A callback invoked upon successful addition of the recipe.
   * @param onFailure A callback invoked upon failure to add the recipe, with an exception.
   */
  fun addRecipe(recipe: Recipe, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    // Convert Recipe to a map, replacing Ingredient objects with their IDs
    val recipeMap = recipeToMap(recipe)
    val newDocRef = db.collection(collectionPath).document()
    recipe.recipeId = newDocRef.id
    newDocRef
        .set(recipeMap)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure(it) }
  }

  /**
   * Retrieves a recipe from Firestore by its ID.
   *
   * Fetches the recipe document from Firestore, deserializes it back into a Recipe object, and then
   * invokes the onSuccess callback with it. If the recipe is not found or an error occurs,
   * onFailure is called with an exception.
   *
   * @param recipeId The ID of the recipe to retrieve.
   * @param onSuccess A callback invoked with the retrieved Recipe object upon success.
   * @param onFailure A callback invoked upon failure to retrieve the recipe, with an exception.
   */
  fun getRecipe(recipeId: String, onSuccess: (Recipe?) -> Unit, onFailure: (Exception) -> Unit) {
    db.collection(collectionPath)
        .document(recipeId)
        .get()
        .addOnSuccessListener { documentSnapshot ->
          val recipeMap =
              documentSnapshot.data
                  ?: return@addOnSuccessListener onFailure(Exception("Recipe not found"))
          // Reconstruct the Recipe object, fetching Ingredient objects as needed
          mapToRecipe(recipeMap, onSuccess, onFailure)
        }
        .addOnFailureListener { onFailure(it) }
  }

  /**
   * Fetches the recipes saved by the user from Firestore. The recipes are fetched by their IDs.
   *
   * @param ids The list of recipe IDs to fetch.
   * @param onSuccess A callback function invoked with the list of recipes on success.
   * @param onFailure A callback function invoked on failure to fetch the recipes, with an
   *   exception.
   */
  fun getSavedRecipes(
      ids: List<String>,
      onSuccess: (List<Recipe>, DocumentSnapshot?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Fetch the recipes with the given IDs
    db.collection(collectionPath)
        .whereIn("recipeId", ids)
        .get()
        .addOnSuccessListener { addSuccessListener(it, onSuccess, onFailure) }
        .addOnFailureListener { exception -> onFailure(exception) }
  }

  /**
   * Fetches the top rated recipes from Firestore. The recipes are ordered by their rating in
   * descending order.
   *
   * @param lastRecipe The last recipe fetched in the previous query. If null, fetches the first
   *   page of recipes.
   * @param onSuccess A callback function invoked with the list of recipes on success.
   * @param onFailure A callback function invoked on failure to fetch the recipes, with an
   *   exception.
   */
  fun getRatedRecipes(
      lastRecipe: DocumentSnapshot?,
      onSuccess: (List<Recipe>, DocumentSnapshot?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Create a query to fetch the top rated recipes
    var queryRef =
        db.collection(collectionPath).orderBy("rating", Query.Direction.DESCENDING).limit(6)

    // If lastRecipe is not null, start the query after the last recipe fetched
    if (lastRecipe != null) {
      queryRef = queryRef.startAfter(lastRecipe)
    }

    queryRef
        .get()
        .addOnSuccessListener { addSuccessListener(it, onSuccess, onFailure) }
        .addOnFailureListener { onFailure(it) }
  }

  /**
   * Fetches all the recipes that contain the given query in their title.
   *
   * @param query The query string to search for in the recipe titles.
   * @param lastRecipe The last recipe fetched in the previous query. If null, fetches the first
   *   page of recipes.
   * @param onSuccess A callback function invoked with the list of recipes on success.
   * @param onFailure A callback function invoked on failure to fetch the recipes, with an
   *   exception.
   */
  fun getFilteredRecipes(
      query: String,
      lastRecipe: DocumentSnapshot?,
      onSuccess: (List<Recipe>, DocumentSnapshot?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Create Query for recipes that contain the input query in their title
    var queryRef =
        db.collection(collectionPath)
            .whereArrayContainsAny("searchItems", listOf(query.lowercase(Locale.ROOT)))
            .limit(6) // Limit the number of documents fetched

    // If lastRecipe is not null, start the query after the last recipe fetched
    if (lastRecipe != null) {
      queryRef = queryRef.startAfter(lastRecipe)
    }

    queryRef
        .get()
        .addOnSuccessListener { addSuccessListener(it, onSuccess, onFailure) }
        .addOnFailureListener { onFailure(it) }
  }

  /**
   * A helper function that adds the recipes to the list of recipes
   *
   * @param snapshot: the snapshot of the query
   * @param onSuccess: the callback function to be called on success
   * @param onFailure: the callback function to be called on failure
   */
  private fun addSuccessListener(
      snapshot: QuerySnapshot,
      onSuccess: (List<Recipe>, DocumentSnapshot?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val recipes = mutableListOf<Recipe>()
    val docs = snapshot.documents

    docs.forEach { recipeMap ->
      // Extract the data from the document
      val data = recipeMap.data
      if (data != null) {
        // Convert the data to a Recipe object
        mapToRecipe(
            data,
            { recipe ->
              if (recipe != null) {
                recipes.add(recipe)
              }
            },
            onFailure)
      }
    }
    // Call the success callback with the list of recipes
    onSuccess(recipes, docs.lastOrNull())
  }

  private fun recipeToMap(recipe: Recipe): Map<String, Any> {
    return mapOf(
        "recipeId" to recipe.recipeId,
        "title" to recipe.title,
        "description" to recipe.description,
        "ingredients" to recipe.ingredients.map { it.toMap() },
        "steps" to recipe.steps.map { it.toMap() },
        "tags" to recipe.tags,
        "rating" to recipe.rating,
        "userid" to recipe.userid,
        "imageUrl" to recipe.imageUrl,
        "ingredientIds" to recipe.ingredients.map { it.ingredient.id })
  }

  private fun IngredientMetaData.toMap(): Map<String, Any> =
      mapOf(
          "quantity" to this.quantity,
          "measure" to this.measure.name, // Store the measure as a string
          "ingredient" to this.ingredient.toMap())

  private fun Ingredient.toMap(): Map<String, Any> =
      mapOf(
          "name" to this.name,
          "id" to this.id,
          "vegan" to this.vegan,
          "vegetarian" to this.vegetarian)

  private fun Step.toMap(): Map<String, Any> =
      mapOf(
          "stepNumber" to this.stepNumber, "description" to this.description, "title" to this.title)

  fun mapToRecipe(
      map: Map<String, Any>,
      onSuccess: (Recipe?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      // Extract the raw ingredients list
      val rawIngredientsList = map["ingredients"]
      Log.d("RecipeRepository", "raw Ids $rawIngredientsList")

      // Parse ingredient meta data
      val ingredientMetaDataList =
          if (rawIngredientsList is List<*>) {
            rawIngredientsList.mapNotNull { rawIngredient ->
              (rawIngredient as? Map<*, *>)?.let { ingredientMap ->
                val quantity = (ingredientMap["quantity"] as? Number)?.toDouble()
                val measure = stringToMeasureUnit(ingredientMap["measure"] as? String)
                val ingredientDetails = ingredientMap["ingredient"] as? Map<*, *>

                if (quantity != null && ingredientDetails != null) {
                  val name = ingredientDetails["name"] as? String ?: ""
                  val vegetarian = ingredientDetails["vegetarian"] as? Boolean ?: false
                  val vegan = ingredientDetails["vegan"] as? Boolean ?: false
                  val id = ingredientDetails["id"] as? String ?: ""

                  val ingredient = Ingredient(name, id, vegetarian, vegan)
                  IngredientMetaData(quantity, measure, ingredient)
                } else null
              }
            }
          } else {
            listOf()
          }

      // Safely process the tags
      val rawTagsList = map["tags"]
      val tags =
          if (rawTagsList is List<*>) {
            rawTagsList.mapNotNull { it as? String }
          } else {
            listOf()
          }

      // Safely process the steps
      val rawStepsList = map["steps"]
      val steps =
          if (rawStepsList is List<*>) {
            rawStepsList.mapNotNull { rawStep ->
              (rawStep as? Map<*, *>)?.let { stepMap ->
                val stepNumber = (stepMap["stepNumber"] as? Number)?.toInt()
                val description = stepMap["description"] as? String
                val title = stepMap["title"] as? String
                if (stepNumber != null && description != null && title != null) {
                  Step(stepNumber, description, title)
                } else null
              }
            }
          } else {
            listOf()
          }

      // Construct the Recipe object
      val recipe =
          Recipe(
              recipeId = map["recipeId"] as? String ?: "",
              title = map["title"] as? String ?: "",
              description = map["description"] as? String ?: "",
              ingredients = ingredientMetaDataList,
              steps = steps,
              tags = tags,
              rating = (map["rating"] as? Number)?.toDouble() ?: 0.0,
              userid = map["userid"] as? String ?: "",
              imageUrl = map["imageUrl"] as? String ?: "")
      Log.d("RecipeRepository", " Recipe fetched success $recipe ")

      onSuccess(recipe)
    } catch (e: Exception) {
      onFailure(e)
    }
  }

  private fun stringToMeasureUnit(measure: String?): MeasureUnit {
    return when (measure?.uppercase()) {
      "TEASPOON" -> MeasureUnit.TEASPOON
      "TABLESPOON" -> MeasureUnit.TABLESPOON
      "CUP" -> MeasureUnit.CUP
      "G" -> MeasureUnit.G
      "KG" -> MeasureUnit.KG
      "L" -> MeasureUnit.L
      "ML" -> MeasureUnit.ML
      "PIECES" -> MeasureUnit.PIECES
      "NONE" -> MeasureUnit.NONE
      else -> MeasureUnit.EMPTY
    }
  }

  /**
   * Fetches recipes that contain any of the given ingredient IDs and maps them to Recipe objects.
   *
   * @param ingredientIds The list of ingredient IDs to match against.
   * @param onSuccess A callback function invoked with the list of fetched recipes.
   * @param onFailure A callback function invoked on failure to fetch the recipes, with an
   *   exception.
   */
  private fun fetchAndMapRecipes(
      ingredientIds: List<String>,
      onSuccess: (List<Recipe>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    if (ingredientIds.isEmpty()) {
      onFailure(IllegalArgumentException())
      return
    }
    // Create a query to fetch the top rated recipes
    db.collection(collectionPath)
        .whereArrayContainsAny("ingredientIds", ingredientIds)
        .get()
        .addOnSuccessListener { querySnapshot ->
          val allRecipes = mutableListOf<Recipe>()

          val docs = querySnapshot.documents
          docs.forEach { recipeMap ->
            val data = recipeMap.data
            if (data != null) {
              mapToRecipe(
                  data,
                  { recipe ->
                    if (recipe != null) {
                      allRecipes.add(recipe)
                    }
                  },
                  onFailure)
            }
          }

          onSuccess(allRecipes)
        }
        .addOnFailureListener { exception -> onFailure(exception) }
  }

  /**
   * Suggests recipes based on the given list of ingredient IDs and the user profile.
   *
   * @param ingredientIds The list of ingredient IDs to match against.
   * @param profile The user profile to consider for preferences.
   * @param onSuccess A callback function invoked with the ranked list of suggested recipes on
   *   success.
   * @param onFailure A callback function invoked on failure to fetch the recipes, with an
   *   exception.
   */
  fun suggestRecipes(
      ingredientIds: List<String>,
      profile: Profile,
      onSuccess: (List<Recipe>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    fetchAndMapRecipes(
        ingredientIds,
        { allRecipes ->
            Log.d("RecipeRepository", "recipe to filter $allRecipes")
            val rankedRecipes = rankRecipes(allRecipes, ingredientIds, profile)
            Log.d("RecipeRepository", "order recipe  $rankedRecipes")
            onSuccess(rankedRecipes)
        },
        onFailure)
  }

  /**
   * Suggests recipes based on the given list of ingredient IDs and the user profile in strict mode.
   *
   * @param ingredientIds The list of ingredient IDs to match exactly.
   * @param profile The user profile to consider for preferences.
   * @param onSuccess A callback function invoked with the ranked list of suggested recipes on
   *   success.
   * @param onFailure A callback function invoked on failure to fetch the recipes, with an
   *   exception.
   */
  fun suggestRecipesStrict(
      ingredientIds: List<String>,
      profile: Profile,
      onSuccess: (List<Recipe>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    fetchAndMapRecipes(
        ingredientIds,
        { allRecipes ->
          val filteredRecipes =
              allRecipes.filter { recipe ->
                val recipeIngredientIds = recipe.ingredients.map { it.ingredient.id }
                recipeIngredientIds.containsAll(ingredientIds) &&
                    ingredientIds.containsAll(recipeIngredientIds)
              }

          val rankedRecipes = rankRecipes(filteredRecipes, ingredientIds, profile)
          onSuccess(rankedRecipes)
        },
        onFailure)
  }

  /**
   * Ranks recipes based on the number of matching ingredients, user preferences, and ratings.
   *
   * @param recipes The list of recipes to rank.
   * @param ingredientIds The list of ingredient IDs to match against.
   * @param profile The user profile to consider for preferences.
   * @return A ranked list of recipes.
   */
  private fun rankRecipes(
      recipes: List<Recipe>,
      ingredientIds: List<String>,
      profile: Profile
  ): List<Recipe> {
    return recipes.sortedWith(
        compareByDescending { recipe ->
          val userPreferencesScore = recipe.tags.count { tag -> profile.filter.contains(tag) }
          val ratingScore = recipe.rating
          val matchingIngredientsCount =
              recipe.ingredients.count { ingredientMetaData ->
                ingredientIds.contains(ingredientMetaData.ingredient.id)
              }

          // Combine the scores to form the final ranking score
          matchingIngredientsCount + userPreferencesScore + ratingScore
        })
  }
}
