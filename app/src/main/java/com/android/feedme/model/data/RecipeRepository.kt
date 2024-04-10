package com.android.feedme.model.data

import com.google.firebase.firestore.FirebaseFirestore

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

  fun addRecipe(recipe: Recipe, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    // Convert Recipe to a map, replacing Ingredient objects with their IDs
    val recipeMap = recipeToMap(recipe)
    db.collection(collectionPath)
        .document(recipe.recipeId)
        .set(recipeMap)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure(it) }
  }

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

  private fun recipeToMap(recipe: Recipe): Map<String, Any> {
    return mapOf(
        "recipeId" to recipe.recipeId,
        "title" to recipe.title,
        "description" to recipe.description,
        "ingredients" to recipe.ingredients.map { it.ingredient.id },
        "steps" to recipe.steps.map { it.toMap() },
        "tags" to recipe.tags,
        "time" to recipe.time,
        "rating" to recipe.rating,
        "userid" to recipe.userid,
        "difficulty" to recipe.difficulty,
        "imageUrl" to recipe.imageUrl)
  }

  private fun IngredientMetaData.toMap(): Map<String, Any> =
      mapOf(
          "quantity" to this.quantity,
          "measure" to this.measure.name, // Store the measure as a string
          "ingredientId" to this.ingredient.id)

  private fun Step.toMap(): Map<String, Any> =
      mapOf(
          "stepNumber" to this.stepNumber, "description" to this.description, "title" to this.title)

  private fun mapToRecipe(
      map: Map<String, Any>,
      onSuccess: (Recipe?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Extract ingredient IDs from the map
    val ingredientMaps = map["ingredients"] as? List<Map<String, Any>> ?: listOf()
    val ingredientIds = ingredientMaps.mapNotNull { it["ingredientId"] as? String }
    // Fetch ingredients from the repository
    ingredientsRepository.getIngredients(
        ingredientIds,
        onSuccess = { ingredients ->
          try {
            // Convert the map and fetched ingredients into a Recipe object
            val recipe =
                Recipe(
                    recipeId = map["recipeId"] as String,
                    title = map["title"] as String,
                    description = map["description"] as String,
                    ingredients = ingredients, // Directly use the fetched ingredients
                    steps =
                        (map["steps"] as List<Map<String, Any>>).map { stepMap ->
                          Step(
                              stepNumber = stepMap["stepNumber"] as Int,
                              description = stepMap["description"] as String,
                              title = stepMap["title"] as String)
                        },
                    tags = map["tags"] as List<String>,
                    time = map["time"] as Double,
                    rating = map["rating"] as Double,
                    userid = map["userid"] as String,
                    difficulty = map["difficulty"] as String,
                    imageUrl = map["imageUrl"] as String)
            onSuccess(recipe)
          } catch (e: Exception) {
            onFailure(e)
          }
        },
        onFailure = {
          // Handle failure in fetching ingredients
          onFailure(it)
        })
  }
}
