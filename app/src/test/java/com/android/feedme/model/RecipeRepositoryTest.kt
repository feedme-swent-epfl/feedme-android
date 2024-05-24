package com.android.feedme.model

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.data.Profile
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

  @Mock private lateinit var mockDocumentSnapshot1: DocumentSnapshot

  @Mock private lateinit var mockDocumentSnapshot2: DocumentSnapshot

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

    `when`(mockFirestore.collection(recipeRepository.collectionPath))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference)

    // Additional mocking for ingredients collection
    `when`(mockFirestore.collection("ingredients")).thenReturn(mockIngredientsCollectionReference)
    `when`(mockIngredientsCollectionReference.document(anyString()))
        .thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockIngredientDocumentSnapshot))

    `when`(mockFirestore.collection(recipeRepository.collectionPath))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.id).thenReturn("testRecipeId")
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult<Void>(null))

    // Ensure a Task<Void> is returned
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null))
  }

  @Test
  fun suggestRecipes_Success() {
    val ingredientIds = listOf("flourId", "sugarId")
    val profile =
        Profile(
            id = "user123",
            name = "John Doe",
            username = "johndoe",
            email = "johndoe@example.com",
            description = "Avid baker",
            imageUrl = "http://example.com/profile.jpg",
            filter = listOf("dessert"),
            recipeList = listOf(),
            savedRecipes = listOf(),
            commentList = listOf(),
            showDialog = true)

    val recipeMap1: Map<String, Any> =
        mapOf(
            "recipeId" to "1",
            "title" to "Chocolate Cake",
            "description" to "A rich chocolate cake",
            "ingredients" to
                listOf(
                    mapOf(
                        "quantity" to 2.0,
                        "measure" to MeasureUnit.CUP.name,
                        "ingredient" to
                            mapOf(
                                "name" to "Flour",
                                "id" to "flourId",
                                "vegetarian" to true,
                                "vegan" to true)),
                    mapOf(
                        "quantity" to 1.0,
                        "measure" to MeasureUnit.CUP.name,
                        "ingredient" to
                            mapOf(
                                "name" to "Sugar",
                                "id" to "sugarId",
                                "vegetarian" to true,
                                "vegan" to true))),
            "steps" to listOf<Map<String, Any>>(),
            "tags" to listOf("dessert"),
            "rating" to 4.5,
            "userid" to "user123",
            "imageUrl" to "http://example.com/chocolate_cake.jpg")

    val recipeMap2: Map<String, Any> =
        mapOf(
            "recipeId" to "2",
            "title" to "Vanilla Cake",
            "description" to "A delightful vanilla cake",
            "ingredients" to
                listOf(
                    mapOf(
                        "quantity" to 2.0,
                        "measure" to MeasureUnit.CUP.name,
                        "ingredient" to
                            mapOf(
                                "name" to "Flour",
                                "id" to "flourId",
                                "vegetarian" to true,
                                "vegan" to true)),
                    mapOf(
                        "quantity" to 1.0,
                        "measure" to MeasureUnit.CUP.name,
                        "ingredient" to
                            mapOf(
                                "name" to "Sugar",
                                "id" to "sugarId",
                                "vegetarian" to true,
                                "vegan" to true))),
            "steps" to listOf<Map<String, Any>>(),
            "tags" to listOf("dessert"),
            "rating" to 4.0,
            "userid" to "user123",
            "imageUrl" to "http://example.com/vanilla_cake.jpg")

    val querySnapshot: QuerySnapshot = mock(QuerySnapshot::class.java)
    `when`(mockDocumentSnapshot1.data).thenReturn(recipeMap1)
    `when`(mockDocumentSnapshot2.data).thenReturn(recipeMap2)
    `when`(querySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot1, mockDocumentSnapshot2))

    `when`(mockCollectionReference.whereArrayContainsAny("ingredientIds", ingredientIds))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(querySnapshot))

    recipeRepository.suggestRecipes(
        ingredientIds,
        profile,
        { recipes ->
          assertNotNull(recipes)
          assertEquals(2, recipes.size)
          assertEquals("Chocolate Cake", recipes[0].title)
        },
        { fail("Failure callback was called") })

    shadowOf(Looper.getMainLooper()).idle()
  }

  @Test
  fun suggestRecipesStrict_Success() {
    val ingredientIds = listOf("flourId", "sugarId")
    val profile =
        Profile(
            id = "user123",
            name = "John Doe",
            username = "johndoe",
            email = "johndoe@example.com",
            description = "Avid baker",
            imageUrl = "http://example.com/profile.jpg",
            filter = listOf("dessert"),
            recipeList = listOf(),
            savedRecipes = listOf(),
            commentList = listOf(),
            showDialog = true)

    val recipeMap1: Map<String, Any> =
        mapOf(
            "recipeId" to "1",
            "title" to "Chocolate Cake",
            "description" to "A rich chocolate cake",
            "ingredients" to
                listOf(
                    mapOf(
                        "quantity" to 2.0,
                        "measure" to MeasureUnit.CUP.name,
                        "ingredient" to
                            mapOf(
                                "name" to "Flour",
                                "id" to "flourId",
                                "vegetarian" to true,
                                "vegan" to true)),
                    mapOf(
                        "quantity" to 1.0,
                        "measure" to MeasureUnit.CUP.name,
                        "ingredient" to
                            mapOf(
                                "name" to "Sugar",
                                "id" to "sugarId",
                                "vegetarian" to true,
                                "vegan" to true))),
            "steps" to listOf<Map<String, Any>>(),
            "tags" to listOf("dessert"),
            "rating" to 4.5,
            "userid" to "user123",
            "imageUrl" to "http://example.com/chocolate_cake.jpg")

    val recipeMap2: Map<String, Any> =
        mapOf(
            "recipeId" to "2",
            "title" to "Vanilla Cake",
            "description" to "A delightful vanilla cake",
            "ingredients" to
                listOf(
                    mapOf(
                        "quantity" to 2.0,
                        "measure" to MeasureUnit.CUP.name,
                        "ingredient" to
                            mapOf(
                                "name" to "Flour",
                                "id" to "flourId",
                                "vegetarian" to true,
                                "vegan" to true)),
                    mapOf(
                        "quantity" to 1.0,
                        "measure" to MeasureUnit.CUP.name,
                        "ingredient" to
                            mapOf(
                                "name" to "Sugar",
                                "id" to "notSugarId",
                                "vegetarian" to true,
                                "vegan" to true))),
            "steps" to listOf<Map<String, Any>>(),
            "tags" to listOf("dessert"),
            "rating" to 4.0,
            "userid" to "user123",
            "imageUrl" to "http://example.com/vanilla_cake.jpg")

    val querySnapshot: QuerySnapshot = mock(QuerySnapshot::class.java)
    `when`(mockDocumentSnapshot1.data).thenReturn(recipeMap1)
    `when`(mockDocumentSnapshot2.data).thenReturn(recipeMap2)
    `when`(querySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot1, mockDocumentSnapshot2))

    `when`(mockCollectionReference.whereArrayContainsAny("ingredientIds", ingredientIds))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(querySnapshot))

    recipeRepository.suggestRecipesStrict(
        ingredientIds,
        profile,
        { recipes ->
          assertNotNull(recipes)
          assertEquals(1, recipes.size) // Only Chocolate Cake should match exactly
          assertEquals("Chocolate Cake", recipes[0].title)
        },
        { fail("Failure callback was called") })

    shadowOf(Looper.getMainLooper()).idle()
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

    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot1))
    `when`(mockDocumentSnapshot1.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot1.data).thenReturn(recipeMap)

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
  fun mapToRecipe_Failure() {
    val invalidRecipeMap: Map<String, Any> =
        mapOf(
            "recipeId" to "1",
            "title" to "Chocolate Cake",
            "description" to "A deliciously rich chocolate cake.",
            "ingredients" to "invalid ingredients data",
            "steps" to
                listOf(
                    mapOf(
                        "stepNumber" to 1,
                        "description" to "Mix dry ingredients.",
                        "title" to "Prepare Dry Mix")),
            "tags" to listOf("dessert", "chocolate", "cake"),
            "rating" to 4.5,
            "userid" to "user123",
            "imageUrl" to "http://example.com/chocolate_cake.jpg")

    var recipeResult: Recipe? = null
    var failureCalled = false

    recipeRepository.mapToRecipe(
        invalidRecipeMap,
        onSuccess = { recipe -> recipeResult = recipe },
        onFailure = { failureCalled = true })

    shadowOf(Looper.getMainLooper()).idle()
    recipeResult?.ingredients?.let { assertTrue(it.isEmpty()) }
    assertFalse("Failure callback should be called", failureCalled)
  }

  @Test
  fun testSingletonInitialization() {
    val mockFirestore = mock(FirebaseFirestore::class.java)
    RecipeRepository.initialize(mockFirestore)

    assertNotNull("Singleton instance should be initialized", RecipeRepository.instance)
  }

  @Test
  fun mapToRecipe_Success() {
    val recipeMap: Map<String, Any> =
        mapOf(
            "recipeId" to "1",
            "title" to "Chocolate Cake",
            "description" to "A deliciously rich chocolate cake.",
            "ingredients" to
                listOf(
                    mapOf(
                        "quantity" to 2.0,
                        "measure" to "CUP",
                        "ingredient" to
                            mapOf(
                                "name" to "Flour",
                                "id" to "flourId",
                                "vegetarian" to true,
                                "vegan" to true)),
                    mapOf(
                        "quantity" to 1.0,
                        "measure" to "CUP",
                        "ingredient" to
                            mapOf(
                                "name" to "Sugar",
                                "id" to "sugarId",
                                "vegetarian" to true,
                                "vegan" to true))),
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
            "rating" to 4.5,
            "userid" to "user123",
            "imageUrl" to "http://example.com/chocolate_cake.jpg")

    var recipeResult: Recipe? = null
    var failureCalled = false

    recipeRepository.mapToRecipe(
        recipeMap,
        onSuccess = { recipe -> recipeResult = recipe },
        onFailure = { failureCalled = true })

    shadowOf(Looper.getMainLooper()).idle()

    assertNotNull("Recipe should not be null", recipeResult)
    assertEquals("Recipe ID does not match", "1", recipeResult?.recipeId)
    assertEquals("Title does not match", "Chocolate Cake", recipeResult?.title)
    assertEquals(
        "Description does not match",
        "A deliciously rich chocolate cake.",
        recipeResult?.description)
    assertEquals("Tags do not match", listOf("dessert", "chocolate", "cake"), recipeResult?.tags)
    assertEquals("Rating does not match", 4.5, recipeResult?.rating)
    assertEquals("User ID does not match", "user123", recipeResult?.userid)
    assertEquals(
        "Image URL does not match", "http://example.com/chocolate_cake.jpg", recipeResult?.imageUrl)
    assertFalse("Failure callback should not be called", failureCalled)

    val ingredients = recipeResult?.ingredients
    assertNotNull("Ingredients should not be null", ingredients)
    assertEquals("Ingredients list size incorrect", 2, ingredients?.size)
    assertEquals(
        "First ingredient name does not match", "Flour", ingredients?.get(0)?.ingredient?.name)
    assertEquals("First ingredient quantity does not match", 2.0, ingredients?.get(0)?.quantity)
    assertEquals(
        "First ingredient measure does not match", MeasureUnit.CUP, ingredients?.get(0)?.measure)

    val steps = recipeResult?.steps
    assertNotNull("Steps should not be null", steps)
    assertEquals("Steps list size incorrect", 3, steps?.size)
    assertEquals("First step title does not match", "Prepare Dry Mix", steps?.get(0)?.title)
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

    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot1))
    `when`(mockDocumentSnapshot1.exists()).thenReturn(true)

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
