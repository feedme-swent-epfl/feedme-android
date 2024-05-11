package com.android.feedme.ui

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.data.RecipeRepository
import com.android.feedme.model.viewmodel.HomeViewModel
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class LandingViewModelTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore

  @Mock private lateinit var mockDocumentReference: DocumentReference

  @Mock private lateinit var mockCollectionReference: CollectionReference

  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot

  @Mock private lateinit var mockIngredientsCollectionReference: CollectionReference

  @Mock private lateinit var mockIngredientDocumentSnapshot: DocumentSnapshot

  @Mock private lateinit var landingViewModel: HomeViewModel
  private lateinit var recipeRepository: RecipeRepository
  private lateinit var profileRepository: ProfileRepository

  private val recipeId = "lasagna1"
  private val recipeMap: Map<String, Any> =
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

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }
    RecipeRepository.initialize(mockFirestore)
    ProfileRepository.initialize(mockFirestore)

    recipeRepository = RecipeRepository.instance
    profileRepository = ProfileRepository.instance

    `when`(mockFirestore.collection("recipes")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference)

    // Additional mocking for ingredients collection
    `when`(mockFirestore.collection("ingredients")).thenReturn(mockIngredientsCollectionReference)
    `when`(mockIngredientsCollectionReference.document(anyString()))
        .thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockIngredientDocumentSnapshot))

    `when`(mockFirestore.collection("recipes"))
        .thenReturn(Mockito.mock(CollectionReference::class.java))
    `when`(mockFirestore.collection("recipes").document(anyString()))
        .thenReturn(mockDocumentReference)

    // Here's the critical part: ensure a Task<Void> is returned
    `when`(mockDocumentReference.set(Mockito.any())).thenReturn(Tasks.forResult(null))
  }

  @Test
  fun getRecipe_Success() {

    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot.data).thenReturn(recipeMap)

    landingViewModel = HomeViewModel()
    landingViewModel.fetchRecipe("lasagna1")
    shadowOf(Looper.getMainLooper()).idle()

    println(landingViewModel.recipes.value)
    assertTrue(landingViewModel.recipes.value.first().recipeId == recipeId)
  }

  @Test
  fun searchRecipes_Success() {
    // Setup to return a mock Query object when querying
    val mockQuery: Query = mock(Query::class.java)
    `when`(mockCollectionReference.whereGreaterThanOrEqualTo(anyString(), any()))
        .thenReturn(mockQuery)
    `when`(mockQuery.whereLessThan(anyString(), any())).thenReturn(mockQuery)

    // Mock a QuerySnapshot to return from get()
    val mockQuerySnapshot: QuerySnapshot = mock(QuerySnapshot::class.java)
    val mockDocumentSnapshots: List<DocumentSnapshot> = listOf(mockDocumentSnapshot)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
    `when`(mockQuerySnapshot.documents).thenReturn(mockDocumentSnapshots)

    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot.data).thenReturn(recipeMap)

    landingViewModel = HomeViewModel()
    landingViewModel.searchRecipes("Chocolate")
    shadowOf(Looper.getMainLooper()).idle()

    println(landingViewModel.filteredRecipes.value)
    assertTrue(landingViewModel.filteredRecipes.value.first().recipeId == recipeId)
  }
}
