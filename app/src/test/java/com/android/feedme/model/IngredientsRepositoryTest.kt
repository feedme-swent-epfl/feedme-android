package com.android.feedme.model

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.IngredientsRepository
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import junit.framework.TestCase.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
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
  }

  @Test
  fun addIngredient_Success() {
    val ingredient = Ingredient("Sugar", "Sweetener", "sugarId")
    `when`(mockDocumentReference.set(ingredient)).thenReturn(Tasks.forResult(null))

    var successCalled = false
    ingredientsRepository.addIngredient(ingredient, { successCalled = true }, {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).set(ingredient)
    assertTrue("Success callback was not called", successCalled)
  }

  @Test
  fun getIngredient_Success() {
    val ingredientId = "sugarId"
    val expectedIngredient = Ingredient("Sugar", "Sweetener", ingredientId)
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
    // Setup ingredient IDs
    val ingredientIds = listOf("sugarId", "flourId")
    val sugar = Ingredient("Sugar", "Sweetener", "sugarId")
    val flour = Ingredient("Flour", "Dry", "flourId")

    // Mock Firestore responses for each ingredient ID
    `when`(mockFirestore.collection("ingredients"))
        .thenReturn(Mockito.mock(CollectionReference::class.java))
    `when`(mockFirestore.collection("ingredients").document("sugarId"))
        .thenReturn(mockDocumentReference)
    `when`(mockFirestore.collection("ingredients").document("flourId"))
        .thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.get())
        .thenReturn(Tasks.forResult(mockDocumentSnapshot))
        .thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.toObject(Ingredient::class.java))
        .thenReturn(sugar)
        .thenReturn(flour)

    val latch = CountDownLatch(1)
    var ingredientsMetaDataList: List<IngredientMetaData>? = null

    // Execute fetchIngredients
    ingredientsRepository.getIngredients(ingredientIds) { ingredients ->
      ingredientsMetaDataList = ingredients
      latch.countDown()
    }

    // Wait for async operations to complete
    latch.await(2, TimeUnit.SECONDS)
    shadowOf(Looper.getMainLooper()).idle()

    // Verify
    assertNotNull("IngredientsMetaDataList is null", ingredientsMetaDataList)
    assertEquals("IngredientsMetaDataList size incorrect", 2, ingredientsMetaDataList?.size)

    // Additional checks can be performed on the contents of ingredientsMetaDataList
  }

  // Add more tests for update, delete, etc.
}
