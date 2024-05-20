package com.android.feedme.model

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientsRepository
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class IngredientsRepositoryTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore

  @Mock private lateinit var mockDocumentReference: DocumentReference

  @Mock private lateinit var mockCollectionReference: CollectionReference

  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot

  private lateinit var ingredientsRepository: IngredientsRepository
  @Mock private lateinit var mockQuery: Query

  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    ingredientsRepository = IngredientsRepository(mockFirestore)

    `when`(mockFirestore.collection("ingredients")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereGreaterThanOrEqualTo(anyString(), anyString()))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereLessThan(anyString(), anyString()))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereEqualTo(anyString(), anyString()))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.limit(5)).thenReturn(mockQuery)
    `when`(mockCollectionReference.limit(1)).thenReturn(mockQuery)
    `when`(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.id).thenReturn("TEST_ID")

    // Ensure `whereIn` mock setup is included
    `when`(mockCollectionReference.whereIn(anyString(), anyList())).thenReturn(mockQuery)
  }

  @Test
  fun addIngredient_Success() {
    val ingredient = Ingredient("Sugar", "sugarId", false, false)
    `when`(mockDocumentReference.set(ingredient)).thenReturn(Tasks.forResult(null))

    var successCalled = false
    ingredientsRepository.addIngredient(ingredient, { successCalled = true }, {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).set(ingredient)
    assertTrue("Success callback was not called", successCalled)
    assertTrue("Not a matching id", ingredient.id == "TEST_ID")
  }

  @Test
  fun getIngredient_Success() {
    val ingredientId = "sugarId"
    val expectedIngredient = Ingredient("Sugar", ingredientId, false, false)
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.toObject(Ingredient::class.java)).thenReturn(expectedIngredient)

    ingredientsRepository.getIngredient(
        ingredientId,
        { ingredient -> assertEquals(expectedIngredient, ingredient) },
        { fail("Failure callback was called") })

    shadowOf(Looper.getMainLooper()).idle()
  }

  @Test
  fun getIngredients_Success() {
    val ingredientIds = listOf("sugarId", "flourId")
    val sugar = Ingredient("Sugar", "sugarId", false, false)
    val flour = Ingredient("Flour", "flourId", false, false)

    // Setup responses for fetching ingredients using whereIn
    `when`(mockFirestore.collection("ingredients")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereIn("id", ingredientIds)).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    // Mock the query snapshot to return the sugar and flour documents
    val sugarDocumentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
    val flourDocumentSnapshot = Mockito.mock(DocumentSnapshot::class.java)

    `when`(sugarDocumentSnapshot.toObject(Ingredient::class.java)).thenReturn(sugar)
    `when`(flourDocumentSnapshot.toObject(Ingredient::class.java)).thenReturn(flour)
    `when`(mockQuerySnapshot.documents)
        .thenReturn(listOf(sugarDocumentSnapshot, flourDocumentSnapshot))

    // Capturing onSuccess callback execution
    var ingredientsResult: List<Ingredient>? = null
    ingredientsRepository.getIngredients(
        ingredientIds,
        onSuccess = { ingredients -> ingredientsResult = ingredients },
        onFailure = { fail("Failure callback should not be called") })

    // Wait for async tasks to complete
    shadowOf(Looper.getMainLooper()).idle()

    // Assertions
    assertNotNull("Ingredients result should not be null", ingredientsResult)
    assertEquals("Ingredients list size incorrect", 2, ingredientsResult?.size)
    assertTrue("Expected ingredient not found", ingredientsResult?.contains(sugar) ?: false)
    assertTrue("Expected ingredient not found", ingredientsResult?.contains(flour) ?: false)
  }

  @Test
  fun addIngredient_Failure() {
    val ingredient = Ingredient("Sugar", "sugarId", false, false)
    val exception = Exception("Firestore add failure")
    `when`(mockDocumentReference.set(ingredient)).thenReturn(Tasks.forException(exception))

    var failureCalled = false
    ingredientsRepository.addIngredient(
        ingredient,
        onSuccess = { fail("Success callback should not be called on failure") },
        onFailure = { failureCalled = true })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue("Failure callback was not called", failureCalled)
  }

  @Test
  fun getIngredient_Failure() {
    val ingredientId = "nonexistentId"
    val exception = Exception("Firestore operation failed")
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forException(exception))

    var failureCalled = false
    ingredientsRepository.getIngredient(
        ingredientId,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { failureCalled = true })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue("Failure callback was not called", failureCalled)
  }

  @Test
  fun getIngredients_Failure() {
    val ingredientIds = listOf("sugarId", "flourId")
    val exception = Exception("Firestore fetch failed")

    // Setup mocks to simulate a failure for the whereIn query
    `when`(mockFirestore.collection("ingredients")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.whereIn("id", ingredientIds)).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forException(exception))

    var failureCalled = false

    ingredientsRepository.getIngredients(
        ingredientIds,
        onSuccess = { fail("Success callback should not be called in failure scenario") },
        onFailure = { failureCalled = true })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue("Failure callback was not called as expected", failureCalled)
  }

  @Test
  fun testSingletonInitialization() {
    val mockFirestore = mock(FirebaseFirestore::class.java)
    IngredientsRepository.initialize(mockFirestore)

    assertNotNull("Singleton instance should be initialized", IngredientsRepository.instance)
  }

  @Test
  fun fetchIngredients_Success() {
    // Mocking successful query snapshot
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.data)
        .thenReturn(mapOf("name" to "Sugar", "vegan" to false, "vegetarian" to true))
    `when`(mockDocumentSnapshot.id).thenReturn("sugarId")

    // Test fetchIngredients function
    ingredientsRepository.fetchIngredients(
        mockQuery,
        onSuccess = { ingredients ->
          assertEquals(1, ingredients.size)
          assertTrue(ingredients.first() == Ingredient("Sugar", "sugarId", false, true))
        },
        onFailure = { fail("Should not fail") })

    shadowOf(Looper.getMainLooper()).idle()
  }

  @Test
  fun fetchIngredients_Failure() {
    // Mocking failure for query snapshot
    val exception = Exception("Firestore operation failed")
    `when`(mockQuery.get()).thenReturn(Tasks.forException(exception))

    // Test fetchIngredients function failure
    ingredientsRepository.fetchIngredients(
        mockQuery,
        onSuccess = { fail("Should not succeed") },
        onFailure = { assertEquals(exception, it) })
    shadowOf(Looper.getMainLooper()).idle()
  }

  @Test
  fun getFilteredIngredients_Success() {
    // Mocking successful query snapshot
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.data)
        .thenReturn(mapOf("name" to "Sugar", "vegan" to false, "vegetarian" to true))
    `when`(mockDocumentSnapshot.id).thenReturn("sugarId")

    // Test getFilteredIngredients function
    ingredientsRepository.getFilteredIngredients(
        "Sugar",
        onSuccess = { ingredients ->
          assertEquals(1, ingredients.size)
          assertTrue(ingredients.first() == Ingredient("Sugar", "sugarId", false, true))
        },
        onFailure = { fail("Should not fail") })
  }

  @Test
  fun getFilteredIngredients_Failure() {
    // Mocking failure for query snapshot
    val exception = Exception("Firestore operation failed")
    `when`(mockQuery.get()).thenReturn(Tasks.forException(exception))

    // Test getFilteredIngredients function failure
    ingredientsRepository.getFilteredIngredients(
        "Sugar",
        onSuccess = { fail("Should not succeed") },
        onFailure = { assertEquals(exception, it) })
  }

  @Test
  fun getExactFilteredIngredients_Success() {
    // Mocking successful query snapshot
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.data)
        .thenReturn(mapOf("name" to "Sugar", "vegan" to false, "vegetarian" to true))
    `when`(mockDocumentSnapshot.id).thenReturn("sugarId")

    // Test getExactFilteredIngredients function
    ingredientsRepository.getExactFilteredIngredients(
        "Sugar",
        onSuccess = { ingredients ->
          assertEquals(1, ingredients.size)
          assertTrue(ingredients.first() == Ingredient("Sugar", "sugarId", false, true))
        },
        onFailure = { fail("Should not fail") })
  }

  @Test
  fun getExactFilteredIngredients_Failure() {
    // Mocking failure for query snapshot
    val exception = Exception("Firestore operation failed")
    `when`(mockQuery.get()).thenReturn(Tasks.forException(exception))

    // Test getExactFilteredIngredients function failure
    ingredientsRepository.getExactFilteredIngredients(
        "Sugar",
        onSuccess = { fail("Should not succeed") },
        onFailure = { assertEquals(exception, it) })
  }

  @Test
  fun documentSnapshotToIngredient_Success() {
    // Mocking a document snapshot with valid data
    val mockDocumentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
    `when`(mockDocumentSnapshot.data)
        .thenReturn(mapOf("name" to "Sugar", "vegan" to false, "vegetarian" to true))
    `when`(mockDocumentSnapshot.id).thenReturn("sugarId")

    // Test documentSnapshotToIngredient function
    val ingredient = ingredientsRepository.documentSnapshotToIngredient(mockDocumentSnapshot)
    assertEquals(Ingredient("Sugar", "sugarId", false, true), ingredient)
  }

  @Test
  fun documentSnapshotToIngredient_NullName() {
    // Mocking a document snapshot with null name
    val mockDocumentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
    `when`(mockDocumentSnapshot.data).thenReturn(mapOf("vegan" to false, "vegetarian" to true))
    `when`(mockDocumentSnapshot.id).thenReturn("sugarId")

    // Test documentSnapshotToIngredient function with null name
    val ingredient = ingredientsRepository.documentSnapshotToIngredient(mockDocumentSnapshot)
    assertEquals(null, ingredient)
  }

  @Test
  fun initialize_Success() {
    // Test initialize function
    IngredientsRepository.initialize(mockFirestore)
    assertEquals(mockFirestore, IngredientsRepository.instance.db)
  }
}
