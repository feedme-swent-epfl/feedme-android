package com.android.feedme.model.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.android.feedme.model.viewmodel.displayToast
import com.android.feedme.model.viewmodel.isNetworkAvailable
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage
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
  val databasePath = "recipes/"

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
   * @param recipe The Recipe object to be added to Firestore.
   * @param context The context of the calling activity.
   * @param onSuccess A callback invoked upon successful addition of the recipe.
   * @param onFailure A callback invoked upon failure to add the recipe, with an exception.
   */
  fun addRecipe(
      recipe: Recipe,
      uri: Uri?,
      context: Context = FirebaseFirestore.getInstance().app.applicationContext,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Check if the user is offline
    if (!isNetworkAvailable(context)) {
      Log.d("addRecipe", "Offline mode: Cannot add recipe")
      displayToast(context)
      return
    }

    // Convert Recipe to a map, replacing Ingredient objects with their IDs

    val newDocRef = db.collection(collectionPath).document()
    recipe.recipeId = newDocRef.id

    if (uri != null) {
      val storageRef =
          FirebaseStorage.getInstance().reference.child((databasePath + recipe.recipeId))
      storageRef
          .putFile(uri)
          .addOnSuccessListener { taskSnapshot ->
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
              recipe.imageUrl = uri.toString()
              mapAndAddRecipe(recipe, newDocRef, onSuccess, onFailure)
            }
          }
          .addOnFailureListener { exception -> onFailure(exception) }
    } else {
      recipe.imageUrl =
          "https://firebasestorage.googleapis.com/v0/b/feedme-33341.appspot.com/o/recipestest%2Fdummy.jpg?alt=media&token=71de581c-9e1e-47c8-a4dc-8cccf1d0b640"
      mapAndAddRecipe(recipe, newDocRef, onSuccess, onFailure)
    }
  }

  /**
   * This method serializes the Recipe object into a map, replacing complex objects like Ingredient
   * with their IDs, and then stores it in Firestore under the recipes collection.
   */
  private fun mapAndAddRecipe(
      recipe: Recipe,
      id: DocumentReference,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val recipeMap = recipeToMap(recipe)
    id.set(recipeMap).addOnSuccessListener { onSuccess() }.addOnFailureListener { onFailure(it) }
  }

  /**
   * Retrieves a recipe from Firestore by its ID.
   *
   * Fetches the recipe document from Firestore, deserializes it back into a Recipe object, and then
   * invokes the onSuccess callback with it. If the recipe is not found or an error occurs,
   * onFailure is called with an exception.
   *
   * @param recipeId The ID of the recipe to retrieve.
   * @param context The context of the calling activity.
   * @param onSuccess A callback invoked with the retrieved Recipe object upon success.
   * @param onFailure A callback invoked upon failure to retrieve the recipe, with an exception.
   */
  fun getRecipe(
      recipeId: String,
      context: Context,
      onSuccess: (Recipe?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Check if the user is offline
    if (!isNetworkAvailable(context)) {
      Log.d("getRecipe", "Offline mode: Cannot fetch recipe")
      displayToast(context)
      return
    }

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
   * @param context The context of the calling activity.
   * @param onSuccess A callback function invoked with the list of recipes on success.
   * @param onFailure A callback function invoked on failure to fetch the recipes, with an
   *   exception.
   */
  fun getSavedRecipes(
      ids: List<String>,
      context: Context,
      onSuccess: (List<Recipe>, DocumentSnapshot?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Function to fetch data from cache
    fun fetchFromCache() {
      db.collection(collectionPath)
          .whereIn("recipeId", ids)
          .get(Source.CACHE)
          .addOnSuccessListener { addSuccessListener(it, onSuccess, onFailure) }
          .addOnFailureListener { exception -> onFailure(exception) }
    }

    if (isNetworkAvailable(context)) {
      db.collection(collectionPath)
          .whereIn("recipeId", ids)
          .get()
          .addOnSuccessListener {
            println("getSavedRecipes: Success fetching online, falling back to cache")
            addSuccessListener(it, onSuccess, onFailure)
          }
          .addOnFailureListener { exception ->
            Log.e("getSavedRecipes", "Error fetching online, falling back to cache", exception)
            fetchFromCache()
          }
    } else {
      fetchFromCache()
    }
  }

  /**
   * Fetches the top rated recipes from Firestore. The recipes are ordered by their rating in
   * descending order.
   *
   * @param lastRecipe The last recipe fetched in the previous query. If null, fetches the first
   *   page of recipes.
   * @param context The context of the calling activity.
   * @param onSuccess A callback function invoked with the list of recipes on success.
   * @param onFailure A callback function invoked on failure to fetch the recipes, with an
   *   exception.
   */
  fun getRatedRecipes(
      lastRecipe: DocumentSnapshot?,
      context: Context,
      onSuccess: (List<Recipe>, DocumentSnapshot?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Check if the user is offline
    if (!isNetworkAvailable(context)) {
      Log.d("getRatedRecipes", "Offline mode: Cannot fetch rated recipes")
      displayToast(context)
      return
    }

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
   * @param context The context of the calling activity.
   * @param lastRecipe The last recipe fetched in the previous query. If null, fetches the first
   *   page of recipes.
   * @param onSuccess A callback function invoked with the list of recipes on success.
   * @param onFailure A callback function invoked on failure to fetch the recipes, with an
   *   exception.
   */
  fun getFilteredRecipes(
      query: String,
      context: Context,
      lastRecipe: DocumentSnapshot?,
      onSuccess: (List<Recipe>, DocumentSnapshot?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Check if the user is offline
    if (!isNetworkAvailable(context)) {
      Log.d("getFilteredRecipes", "Offline mode: Cannot fetch filtered recipes")
      displayToast(context)
      return
    }

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
        "level" to recipe.level,
        "imageUrl" to recipe.imageUrl,
        "ingredientIds" to recipe.ingredients.map { it.ingredient.id },
        "comments" to recipe.comments)
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

      // get the comment if they exist
      val rawCommentsList = map["comments"]
      val comments =
          if (rawCommentsList is List<*>) {
            rawCommentsList.mapNotNull { it as? String }
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
              level = map["level"] as? String ?: "",
              imageUrl = map["imageUrl"] as? String ?: "",
              comments = comments)
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
   * @param context The context of the calling activity.
   * @param onSuccess A callback function invoked with the ranked list of suggested recipes on
   *   success.
   * @param onFailure A callback function invoked on failure to fetch the recipes, with an
   *   exception.
   */
  fun suggestRecipes(
      ingredientIds: List<String>,
      profile: Profile,
      context: Context,
      onSuccess: (List<Recipe>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Check if the user is offline
    if (!isNetworkAvailable(context)) {
      Log.d("getFilteredRecipes", "Offline mode: Cannot fetch filtered recipes")
      displayToast(context)
      return
    }

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
   * @param context The context of the calling activity.
   * @param onSuccess A callback function invoked with the ranked list of suggested recipes on
   *   success.
   * @param onFailure A callback function invoked on failure to fetch the recipes, with an
   *   exception.
   */
  fun suggestRecipesStrict(
      ingredientIds: List<String>,
      profile: Profile,
      context: Context,
      onSuccess: (List<Recipe>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Check if the user is offline
    if (!isNetworkAvailable(context)) {
      Log.d("getFilteredRecipes", "Offline mode: Cannot fetch filtered recipes")
      displayToast(context)
      return
    }

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

  /**
   * Update the comment list in the recipe document in Firestore. Using transaction to ensure the
   * data is consistent.
   *
   * @param recipeId The ID of the recipe to update.
   * @param commentId The comment to add to the recipe.
   */
  fun addCommentToRecipe(
      recipeId: String,
      commentId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val recipeRef = db.collection(collectionPath).document(recipeId)
    db.runTransaction { transaction ->
          val recipe = transaction.get(recipeRef)
          val comments = recipe["comments"] as? List<*> ?: listOf<Comment>()
          transaction.update(recipeRef, "comments", comments + commentId)
        }
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure(it) }
  }
}
