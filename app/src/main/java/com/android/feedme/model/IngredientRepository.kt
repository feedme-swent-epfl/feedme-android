package com.android.feedme.model

import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.google.firebase.firestore.FirebaseFirestore

class IngredientsRepository(private val db: FirebaseFirestore) {

  private val collectionPath = "ingredients"

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

  fun getIngredients(ingredientIds: List<String>, onComplete: (List<IngredientMetaData>) -> Unit) {
    // Fetch each Ingredient by its ID and construct the IngredientMetaData list
    val ingredients = mutableListOf<IngredientMetaData>()
    // Simplified: in a real application, consider fetching all ingredients in parallel or using a
    // batch get
    ingredientIds.forEach { id ->
      db.collection(collectionPath)
          .document(id)
          .get()
          .addOnSuccessListener { documentSnapshot ->
            val ingredient = documentSnapshot.toObject(Ingredient::class.java)
            if (ingredient != null) {
              // Assuming a predefined quantity and measure for simplification
              ingredients.add(IngredientMetaData(1.0, MeasureUnit.CUP, ingredient))
            }
            if (ingredients.size == ingredientIds.size) {
              onComplete(ingredients)
            }
          }
          .addOnFailureListener {
            // Handle failure
          }
    }
  }

  // Additional CRUD operations (update, delete) can be implemented following the same pattern
}
