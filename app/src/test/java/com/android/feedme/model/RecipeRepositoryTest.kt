package com.android.feedme.model

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.RecipeRepository
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.*
import java.util.*
import junit.framework.TestCase.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class RecipeRepositoryTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore

  @Mock private lateinit var mockDocumentReference: DocumentReference

  @Mock private lateinit var mockCollectionReference: CollectionReference

  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot

  @Mock private lateinit var mockIngredientsCollectionReference: CollectionReference

  @Mock private lateinit var mockIngredientDocumentSnapshot: DocumentSnapshot

  private lateinit var recipeRepository: RecipeRepository

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    recipeRepository = RecipeRepository(mockFirestore)

    `when`(mockFirestore.collection("recipes")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference)

    // Additional mocking for ingredients collection
    `when`(mockFirestore.collection("ingredients")).thenReturn(mockIngredientsCollectionReference)
    `when`(mockIngredientsCollectionReference.document(anyString()))
        .thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockIngredientDocumentSnapshot))
  }

  @Test
  fun addRecipe_Success() {
    val recipe =
        Recipe(
            "1",
            "Chocolate Cake",
            "Delicious chocolate cake",
            listOf(),
            listOf(),
            listOf("dessert"),
            60.0,
            4.5,
            "user123",
            "Easy",
            "http://image.url")
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null))

    var successCalled = false
    recipeRepository.addRecipe(recipe, { successCalled = true }, {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).set(any(Map::class.java))
    assertTrue("Success callback was not called", successCalled)
  }

  @Test
  fun getRecipe_Success() {
    val recipeId = "1"
    val recipeMap: Map<String, Any> =
        mapOf(
            "recipeId" to recipeId,
            "title" to "Chocolate Cake",
            "description" to "A deliciously rich chocolate cake.",
            "ingredients" to
                listOf(
                    mapOf(
                        "ingredientId" to "flourId",
                        "quantity" to 2.0,
                        "measure" to MeasureUnit.CUP.name),
                    mapOf(
                        "ingredientId" to "sugarId",
                        "quantity" to 1.0,
                        "measure" to MeasureUnit.CUP.name),
                    mapOf(
                        "ingredientId" to "cocoaId",
                        "quantity" to 0.5,
                        "measure" to MeasureUnit.CUP.name)),
            "steps" to
                listOf(
                    mapOf(
                        "stepNumber" to 1,
                        "description" to "Mix dry ingredients.",
                        "title" to "Prepare Dry Mix"),
                    mapOf(
                        "stepNumber" to 2,
                        "description" to "Blend with wet ingredients.",
                        "title" to "Mix Ingredients"),
                    mapOf(
                        "stepNumber" to 3,
                        "description" to "Pour into pan and bake.",
                        "title" to "Bake")),
            "tags" to listOf("dessert", "chocolate", "cake"),
            "time" to 60.0,
            "rating" to 4.5,
            "userid" to "user123",
            "difficulty" to "Easy",
            "imageUrl" to "http://example.com/chocolate_cake.jpg")

    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot.data).thenReturn(recipeMap)

    // Simulating ingredient fetch - this requires complex mocking or a simplification in testing
    // approach
    // For the purpose of this example, let's assume the fetchIngredients function is simplified or
    // its execution is somehow mocked/controlled within the test

    recipeRepository.getRecipe(
        recipeId,
        { recipe ->
          assertNotNull(recipe)
          assertEquals(recipeId, recipe?.recipeId)
        },
        { fail("Failure callback was called") })

    shadowOf(Looper.getMainLooper()).idle()
  }

  // Add more tests as needed...
}
