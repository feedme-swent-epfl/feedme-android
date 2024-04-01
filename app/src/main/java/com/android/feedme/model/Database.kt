import com.android.feedme.model.data.Ingredient
import com.google.firebase.firestore.FirebaseFirestore
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.Step
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirestoreDatabase {

    private val db = FirebaseFirestore.getInstance()

    fun addRecipe(recipe: Recipe) {
        val recipeMap = hashMapOf(
            "recipeId" to recipe.recipeId,
            "name" to recipe.name,
            "shortDescription" to recipe.shortDescription,
            "detailedDescription" to recipe.detailedDescription,
            "ingredients" to recipe.ingredients.map {
                hashMapOf(
                    "quantity" to it.quantity,
                    "measure" to it.measure,
                    "ingredientId" to it.ingredient.id // Only store ingredient ID
                )
            },
            "steps" to recipe.steps.map {
                hashMapOf(
                    "stepNumber" to it.stepNumber,
                    "shortDescription" to it.shortDescription,
                    "detailedDescription" to it.detailedDescription
                )
            },
            "tags" to recipe.tags
        )

        db.collection("recipes").document(recipe.recipeId).set(recipeMap)
            .addOnSuccessListener { /* Handle success */ }
            .addOnFailureListener { e -> /* Handle error */ }
    }

    // Function to fetch a single ingredient by its ID
    private suspend fun getIngredientById(ingredientId: String): Ingredient? {
        return try {
            val document = db.collection("ingredients").document(ingredientId).get().await()
            document.toObject(Ingredient::class.java)
        } catch (e: Exception) {
            null // Handle the error appropriately
        }
    }


    fun addIngredient(ingredient: Ingredient, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        // Create a new document with a specific ID in the 'ingredients' collection
        val ingredientMap = hashMapOf(
            "name" to ingredient.name,
            "type" to ingredient.type,
            "id" to ingredient.id
        )

        db.collection("ingredients").document(ingredient.id).set(ingredientMap)
            .addOnSuccessListener {
                // Call onSuccess callback when the ingredient is successfully added
                onSuccess()
            }
            .addOnFailureListener { e ->
                // Call onFailure callback with the exception if the operation fails
                onFailure(e)
            }
    }
    // Updated getRecipe function using the getIngredientById function
    fun getRecipe(recipeId: String, onSuccess: (Recipe) -> Unit, onFailure: (Exception) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val recipeDocument = db.collection("recipes").document(recipeId).get().await()
                val recipeMap = recipeDocument.data ?: throw Exception("Recipe not found")

                val ingredientsList = mutableListOf<IngredientMetaData>()
                for (ingredientMap in recipeMap["ingredients"] as List<Map<String, String>>) {
                    val ingredientId = ingredientMap["ingredientId"] ?: continue
                    val ingredient = getIngredientById(ingredientId)
                    if (ingredient != null) {
                        ingredientsList.add(
                            IngredientMetaData(
                                quantity = ingredientMap["quantity"] ?: "",
                                measure = ingredientMap["measure"] ?: "",
                                ingredient = ingredient
                            )
                        )
                    }
                }

                val recipe = Recipe(
                    recipeId = recipeMap["recipeId"] as String,
                    name = recipeMap["name"] as String,
                    shortDescription = recipeMap["shortDescription"] as String,
                    detailedDescription = recipeMap["detailedDescription"] as String,
                    ingredients = ingredientsList,
                    steps = (recipeMap["steps"] as List<Map<String, Any>>).map { stepMap ->
                        Step(
                            stepNumber = (stepMap["stepNumber"] as Long).toInt(),
                            shortDescription = stepMap["shortDescription"] as String,
                            detailedDescription = stepMap["detailedDescription"] as String
                        )
                    },
                    tags = recipeMap["tags"] as List<String>
                )

                CoroutineScope(Dispatchers.Main).launch { onSuccess(recipe) }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch { onFailure(e) }
            }
        }
    }

}
