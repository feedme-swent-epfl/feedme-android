package com.android.feedme.model.data

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class RecipeRepository(private val db: FirebaseFirestore) {

  private val ingredientsRepository = IngredientsRepository(db)
  private val collectionPath = "recipes"

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
      onSuccess: (List<Recipe>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection(collectionPath)
        .whereIn("recipeId", ids)
        .get()
        .addOnSuccessListener {
          it.documents.map { recipeMap ->
            val data = recipeMap.data
            if (data != null) {
              val success = { recipe: Recipe? -> onSuccess(listOfNotNull(recipe)) }
              mapToRecipe(data, success, onFailure)
            }
          }
        }
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
    var queryRef =
        db.collection(collectionPath).orderBy("rating", Query.Direction.DESCENDING).limit(6)

    if (lastRecipe != null) {
      queryRef = queryRef.startAfter(lastRecipe)
    }

    queryRef
        .get()
        .addOnSuccessListener {
          it.documents.map { recipeMap ->
            val data = recipeMap.data
            if (data != null) {
              val success = { recipe: Recipe? ->
                onSuccess(listOfNotNull(recipe), it.documents.lastOrNull())
              }
              mapToRecipe(data, success, onFailure)
            }
          }
        }
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
    var queryRef =
        db.collection(collectionPath)
            .whereGreaterThanOrEqualTo("title", query)
            .whereLessThan("title", query + "\uf8ff")
            .limit(6) // Limit the number of documents fetched

    if (lastRecipe != null) {
      queryRef = queryRef.startAfter(lastRecipe)
    }

    queryRef
        .get()
        .addOnSuccessListener {
          it.documents.map { recipeMap ->
            val data = recipeMap.data
            if (data != null) {
              val success = { recipe: Recipe? ->
                onSuccess(listOfNotNull(recipe), it.documents.lastOrNull())
              }
              mapToRecipe(data, success, onFailure)
            }
          }
        }
        .addOnFailureListener { onFailure(it) }
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
        "imageUrl" to recipe.imageUrl)
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

  private fun mapToRecipe(
      map: Map<String, Any>,
      onSuccess: (Recipe?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Attempt to safely extract the list of ingredient IDs
    val rawIngredientsList = map["ingredients"]
    val ingredientIds =
        if (rawIngredientsList is List<*>) {
          rawIngredientsList.mapNotNull { (it as? Map<*, *>)?.get("ingredientId") as? String }
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

    ingredientsRepository.getIngredients(
        ingredientIds,
        onSuccess = { ingredients ->
          try {
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
                    ingredients = ingredients,
                    steps = steps,
                    tags = tags,
                    rating = (map["rating"] as? Number)?.toDouble() ?: 0.0,
                    userid = map["userid"] as? String ?: "",
                    imageUrl = map["imageUrl"] as? String ?: "")

            onSuccess(recipe)
          } catch (e: Exception) {
            onFailure(e)
          }
        },
        onFailure = onFailure)
  }
}
