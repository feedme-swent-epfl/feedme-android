package com.android.feedme.model.data

import com.google.firebase.firestore.FirebaseFirestore

/**
 * A repository class for managing ingredient documents in Firebase Firestore.
 *
 * This class provides methods for adding, retrieving a single ingredient, and retrieving multiple
 * ingredients from Firestore. It uses a singleton pattern to ensure consistent use of the Firestore
 * instance throughout the application.
 *
 * @property db The Firestore database instance used for ingredient operations.
 */
class IngredientsRepository(private val db: FirebaseFirestore) {

  companion object {
    /** The singleton instance of IngredientsRepository. */
    lateinit var instance: IngredientsRepository
      private set

    /**
     * Initializes the singleton instance of IngredientsRepository. This method should be called
     * once, typically during application startup, to ensure the repository is ready for use.
     *
     * @param db The FirebaseFirestore instance to be used by the repository.
     */
    fun initialize(db: FirebaseFirestore) {
      instance = IngredientsRepository(db)
    }
  }

  private val collectionPath = "ingredients"

  /**
   * Adds an ingredient to Firestore.
   *
   * @param ingredient The Ingredient object to be added.
   * @param onSuccess Callback invoked on successful addition of the ingredient.
   * @param onFailure Callback invoked on failure to add the ingredient, with an exception.
   */
  fun addIngredient(ingredient: Ingredient, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    db.collection(collectionPath)
        .document(ingredient.id)
        .set(ingredient)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception -> onFailure(exception) }
  }

  /**
   * Retrieves a single ingredient by its ID from Firestore.
   *
   * @param ingredientId The ID of the ingredient to retrieve.
   * @param onSuccess Callback invoked with the retrieved Ingredient object on success.
   * @param onFailure Callback invoked on failure to retrieve the ingredient, with an exception.
   */
  fun getIngredient(
      ingredientId: String,
      onSuccess: (Ingredient?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection(collectionPath)
        .document(ingredientId)
        .get()
        .addOnSuccessListener { documentSnapshot ->
          val ingredient = documentSnapshot.toObject(Ingredient::class.java)
          onSuccess(ingredient)
        }
        .addOnFailureListener { exception -> onFailure(exception) }
  }

  /**
   * Retrieves multiple ingredients by their IDs from Firestore.
   *
   * This method handles both the successful retrieval of all ingredients and the case where one or
   * more ingredient retrievals fail.
   *
   * @param ingredientIds The list of ingredient IDs to retrieve.
   * @param onSuccess Callback invoked with a list of IngredientMetaData objects on success.
   * @param onFailure Callback invoked on failure to retrieve one or more ingredients, with an
   *   exception.
   */
  fun getIngredients(
      ingredientIds: List<String>,
      onSuccess: (List<IngredientMetaData>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val ingredients = mutableListOf<IngredientMetaData>()
    if (ingredientIds.isEmpty()) {
      onSuccess(ingredients) // Immediate success for empty input
      return
    }

    var completedRequests = 0
    var hasFailed = false

    ingredientIds.forEach { id ->
      db.collection(collectionPath)
          .document(id)
          .get()
          .addOnSuccessListener { documentSnapshot ->
            val ingredient = documentSnapshot.toObject(Ingredient::class.java)
            if (ingredient != null) {
              ingredients.add(IngredientMetaData(1.0, MeasureUnit.CUP, ingredient))
            }
            completedRequests++
            if (completedRequests == ingredientIds.size && !hasFailed) {
              onSuccess(ingredients)
            }
          }
          .addOnFailureListener { exception ->
            if (!hasFailed) {
              hasFailed = true
              onFailure(exception) // Report failure on the first occurrence
            }
          }
    }
  }
}
