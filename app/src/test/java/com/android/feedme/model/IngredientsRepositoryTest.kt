package com.android.feedme.model

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.IngredientsRepository
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
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

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    ingredientsRepository = IngredientsRepository(mockFirestore)

    `when`(mockFirestore.collection("ingredients")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.id).thenReturn("TEST_ID")
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

    // Setup responses for fetching each ingredient by ID
    `when`(mockFirestore.collection("ingredients")).thenReturn(mockCollectionReference)

    // Mock fetching sugar and flour documents
    val sugarDocumentSnapshot = Mockito.mock(DocumentSnapshot::class.java)
    val flourDocumentSnapshot = Mockito.mock(DocumentSnapshot::class.java)

    `when`(sugarDocumentSnapshot.toObject(Ingredient::class.java)).thenReturn(sugar)
    `when`(flourDocumentSnapshot.toObject(Ingredient::class.java)).thenReturn(flour)

    // Simulate fetching ingredients
    `when`(mockCollectionReference.document("sugarId")).thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.document("flourId")).thenReturn(mockDocumentReference)

    `when`(mockDocumentReference.get())
        .thenReturn(Tasks.forResult(sugarDocumentSnapshot))
        .thenReturn(Tasks.forResult(flourDocumentSnapshot))

    // Capturing onSuccess callback execution
    var ingredientsMetaDataResult: List<IngredientMetaData>? = null
    ingredientsRepository.getIngredients(
        ingredientIds,
        onSuccess = { ingredients -> ingredientsMetaDataResult = ingredients },
        onFailure = { /* Should NOT HAPPENED */})

    // Wait for async tasks to complete
    shadowOf(Looper.getMainLooper()).idle()

    // Assertions
    assertNotNull("IngredientsMetaData result should not be null", ingredientsMetaDataResult)
    assertEquals("IngredientsMetaData list size incorrect", 2, ingredientsMetaDataResult?.size)

    // Further assertions can be made regarding the contents of the ingredientsMetaDataResult list
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

    // Setup mocks to simulate a failure for any fetch attempt
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forException(exception))

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
}
