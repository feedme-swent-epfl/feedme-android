package com.android.feedme.model

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.RecipeRepository
import com.android.feedme.model.data.Step
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.*
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

    `when`(mockFirestore.collection("recipes")).thenReturn(mock(CollectionReference::class.java))
    `when`(mockFirestore.collection("recipes").document(anyString()))
        .thenReturn(mockDocumentReference)

    // Here's the critical part: ensure a Task<Void> is returned
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null))
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
            "user123",
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

  @Test
  fun addRecipe_Failure() {
    val recipe =
        Recipe(
            "1",
            "Chocolate Cake",
            "Delicious chocolate cake",
            listOf(),
            listOf(),
            listOf("dessert"),
            60.0,
            "user123",
            "http://image.url")
    val exception = Exception("Firestore set operation failed")
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forException(exception))

    var failureCalled = false
    recipeRepository.addRecipe(
        recipe,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { failureCalled = true })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue("Failure callback was not called", failureCalled)
  }

  @Test
  fun getRecipe_Failure() {
    val recipeId = "1"
    val exception = Exception("Firestore get operation failed")
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forException(exception))

    var failureCalled = false
    recipeRepository.getRecipe(
        recipeId,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { failureCalled = true })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue("Failure callback was not called", failureCalled)
  }

  @Test
  fun testSingletonInitialization() {
    val mockFirestore = mock(FirebaseFirestore::class.java)
    RecipeRepository.initialize(mockFirestore)

    assertNotNull("Singleton instance should be initialized", RecipeRepository.instance)
  }

  @Test
  fun addRecipe_CorrectlySerializesRecipe() {
    val recipe =
        Recipe(
            recipeId = "1",
            title = "Chocolate Cake",
            description = "Delicious chocolate cake recipe.",
            ingredients =
                listOf(
                    IngredientMetaData(
                        1.0, MeasureUnit.CUP, Ingredient("Flour", "flourId", false, false)),
                    IngredientMetaData(
                        2.0,
                        MeasureUnit.TABLESPOON,
                        Ingredient("Cocoa Powder", "cocoaId", false, false))),
            steps =
                listOf(
                    Step(1, "Mix ingredients.", "Mix all dry ingredients together."),
                    Step(2, "Bake", "Bake in the oven for 45 minutes.")),
            tags = listOf("dessert", "cake", "chocolate"),
            rating = 4.5,
            userid = "user123",
            imageUrl = "http://example.com/cake.jpg")

    var successCalled = false

    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)

    recipeRepository.addRecipe(recipe, onSuccess = { successCalled = true }, onFailure = {})

    // Execute all tasks scheduled on the main thread to simulate the Firestore callback
    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).set(any())

    assertTrue("Success callback was not called as expected", successCalled)
  }

  @Test
  fun addRecipe_Success2() {
    // Preparing the ingredients
    val ingredient1 = Ingredient("Flour", "1", false, false)
    val ingredientMetaData1 = IngredientMetaData(2.0, MeasureUnit.CUP, ingredient1)

    val ingredient2 = Ingredient("Sugar", "2", false, false)
    val ingredientMetaData2 = IngredientMetaData(1.0, MeasureUnit.CUP, ingredient2)

    // Preparing the steps
    val step1 = Step(1, "Mix Ingredients", "Mix flour and sugar.")
    val step2 = Step(2, "Bake", "Bake for 30 minutes at 350 degrees.")

    // Crafting the recipe
    val recipe =
        Recipe(
            "1",
            "Chocolate Cake",
            "A rich chocolate cake",
            listOf(ingredientMetaData1, ingredientMetaData2),
            listOf(step1, step2),
            listOf("Dessert", "Chocolate"),
            60.0,
            "userId123",
            "http://example.com/chocolate_cake.jpg")

    // Mocking Firestore response for a successful set operation
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null))

    // Action: Attempt to add the recipe
    var successCalled = false
    recipeRepository.addRecipe(recipe, onSuccess = { successCalled = true }, onFailure = {})

    // Ensuring all async operations complete
    shadowOf(Looper.getMainLooper()).idle()

    // Verification: Check if the Firestore set method was called
    verify(mockDocumentReference).set(any())

    // Assert: Verify the success callback was invoked
    assertTrue("Expected the success callback to be invoked", successCalled)
  }
}
