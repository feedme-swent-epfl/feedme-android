package com.android.feedme.model.data

import com.google.firebase.firestore.FirebaseFirestore

class IngredientsRepository(private val db: FirebaseFirestore) {

  private val collectionPath = "ingredients"

  companion object {
    // Placeholder for the singleton instance
    lateinit var instance: IngredientsRepository
      private set

    // Initialization method to be called once, e.g., in your Application class
    fun initialize(db: FirebaseFirestore) {
      instance = IngredientsRepository(db)
    }
  }

  fun addIngredient(ingredient: Ingredient, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    db.collection(collectionPath)
        .document(ingredient.id)
        .set(ingredient)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception -> onFailure(exception) }
  }

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

  fun getIngredients(
      ingredientIds: List<String>,
      onSuccess: (List<IngredientMetaData>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val ingredients = mutableListOf<IngredientMetaData>()
    if (ingredientIds.isEmpty()) {
      onSuccess(ingredients) // Handle empty input case immediately
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
              onFailure(exception) // Call onFailure at the first failure occurrence
            }
          }
    }
  }
}
