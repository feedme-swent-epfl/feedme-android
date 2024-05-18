package com.android.feedme.model.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

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
  fun addIngredient(
      ingredient: Ingredient,
      onSuccess: (Ingredient) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val newDocRef = db.collection(collectionPath).document()
    ingredient.id = newDocRef.id // Assign the generated ID to the comment
    newDocRef
        .set(ingredient)
        .addOnSuccessListener {
          Log.e("Ingredient was added", " Name : ${ingredient.name}")
          onSuccess(ingredient)
        }
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
  /**
   * Fetches ingredients based on the provided Firestore query reference.
   *
   * @param queryRef The Firestore query reference.
   * @param onSuccess Callback invoked with a list of Ingredient objects on success.
   * @param onFailure Callback invoked on failure to retrieve ingredients, with an exception.
   */
  private fun fetchIngredients(
      queryRef: Query,
      onSuccess: (List<Ingredient>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    queryRef
        .get()
        .addOnSuccessListener { querySnapshot ->
          val ingredients =
              querySnapshot.documents.mapNotNull { documentSnapshot ->
                val data = documentSnapshot.data
                val name = data?.get("name") as? String?
                val id = documentSnapshot.id
                val vegan = data?.get("vegan") as? Boolean ?: false
                val vegetarian = data?.get("vegetarian") as? Boolean ?: false

                if (name != null) {
                  Ingredient(name, id, vegan, vegetarian)
                } else {
                  null
                }
              }
          Log.e("IngredientsRepository", "Size of ingredients: ${ingredients.size}")
          onSuccess(ingredients)
        }
        .addOnFailureListener { exception -> onFailure(exception) }
  }

  /**
   * Fetches the 5 most similar ingredients to the given query.
   *
   * @param query The query string to search for in the ingredient names.
   * @param onSuccess Callback invoked with a list of Ingredient objects on success.
   * @param onFailure Callback invoked on failure to retrieve ingredients, with an exception.
   */
  fun getFilteredIngredients(
      query: String,
      onSuccess: (List<Ingredient>) -> Unit = {},
      onFailure: (Exception) -> Unit = {}
  ) {
    val queryRef =
        db.collection(collectionPath)
            .whereGreaterThanOrEqualTo("name", query.trim())
            .whereLessThan("name", query.trim() + "\uf8ff")
            .limit(5) // Limit the number of documents fetched to 10

    fetchIngredients(queryRef, onSuccess, onFailure)
  }

  /**
   * Fetches the ingredients that exactly match the given query.
   *
   * @param query The query string to search for in the ingredient names.
   * @param onSuccess Callback invoked with a list of Ingredient objects on success that will
   *   contain 1 ingredient.
   * @param onFailure Callback invoked on failure to retrieve ingredients, with an exception.
   */
  fun getExactFilteredIngredients(
      query: String,
      onSuccess: (List<Ingredient>) -> Unit = {},
      onFailure: (Exception) -> Unit = {}
  ) {
    val queryRef = db.collection(collectionPath).whereEqualTo("name", query.trim()).limit(1)

    fetchIngredients(queryRef, onSuccess, onFailure)
  }
}
