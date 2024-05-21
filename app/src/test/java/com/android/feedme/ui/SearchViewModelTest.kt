package com.android.feedme.ui

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.data.RecipeRepository
import com.android.feedme.model.viewmodel.HomeViewModel
import com.android.feedme.model.viewmodel.SearchViewModel
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows

@RunWith(RobolectricTestRunner::class)
class SearchViewModelTest {
  @Mock private lateinit var mockFirestore: FirebaseFirestore

  @Mock private lateinit var mockDocumentReference: DocumentReference

  @Mock private lateinit var mockCollectionReference: CollectionReference

  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot

  @Mock private lateinit var mockIngredientsCollectionReference: CollectionReference

  @Mock private lateinit var mockIngredientDocumentSnapshot: DocumentSnapshot

  @Mock private lateinit var homeViewModel: HomeViewModel
  @Mock private lateinit var searchViewModel: SearchViewModel
  private lateinit var recipeRepository: RecipeRepository
  private lateinit var profileRepository: ProfileRepository

  @Mock private lateinit var mockQuery: Query
  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot

  private val query = "Chocolate"
  private val queryUser = "user"
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

  private val profileMap: Map<String, Any> =
      mapOf(
          "username" to "user123",
          "email" to "user@gmail.com",
          "profileImageUrl" to "http://example.com/user123.jpg",
          "bio" to "I love to cook!",
          "followers" to 0,
          "following" to 0,
          "savedRecipes" to listOf("lasagna1"),
          "createdRecipes" to listOf("lasagna1"))

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
    `when`(mockFirestore.collection("profiles")).thenReturn(mockCollectionReference)
    `when`(mockFirestore.collection("ingredients")).thenReturn(mockIngredientsCollectionReference)

    `when`(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference)
    `when`(mockIngredientsCollectionReference.document(anyString()))
        .thenReturn(mockDocumentReference)

    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockIngredientDocumentSnapshot))

    // Here's the critical part: ensure a Task<Void> is returned
    `when`(mockDocumentReference.set(Mockito.any())).thenReturn(Tasks.forResult(null))

    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)

    // for searchRecipes
    `when`(mockCollectionReference.whereGreaterThanOrEqualTo("title", query)).thenReturn(mockQuery)
    `when`(mockQuery.whereLessThan("title", query + "\uf8ff")).thenReturn(mockQuery)
    `when`(mockCollectionReference.whereArrayContainsAny(eq("tags"), any())).thenReturn(mockQuery)

    // for searchProfiles
    `when`(mockCollectionReference.whereGreaterThanOrEqualTo("username", queryUser))
        .thenReturn(mockQuery)
    `when`(mockQuery.whereLessThan("username", queryUser + "\uf8ff")).thenReturn(mockQuery)

    `when`(mockQuery.limit(6)).thenReturn(mockQuery)
    `when`(mockQuery.limit(10)).thenReturn(mockQuery)
    `when`(mockQuery.limit(6)).thenReturn(mockQuery)

    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))

    homeViewModel = HomeViewModel()
    searchViewModel = SearchViewModel()
  }

  @Test
  fun searchRecipes_Success() {
    `when`(mockDocumentSnapshot.data).thenReturn(recipeMap)
    searchViewModel.searchRecipes(query)
    Shadows.shadowOf(Looper.getMainLooper()).idle()

    println(searchViewModel.filteredRecipes.value)
    TestCase.assertTrue(searchViewModel.filteredRecipes.value.first().recipeId == recipeId)
  }

  @Test
  fun searchProfiles_Success() {
    `when`(mockDocumentSnapshot.data).thenReturn(profileMap)
    searchViewModel.searchProfiles(queryUser)
    Shadows.shadowOf(Looper.getMainLooper()).idle()

    println(searchViewModel.filteredProfiles.value)
    TestCase.assertTrue(searchViewModel.filteredProfiles.value.first().username == "user123")
  }

  @Test
  fun loadMoreRecipes_Success() {
    `when`(mockDocumentSnapshot.data).thenReturn(recipeMap)
    searchViewModel.searchRecipes(query)
    searchViewModel.loadMoreRecipes()
    Shadows.shadowOf(Looper.getMainLooper()).idle()

    println(searchViewModel.filteredRecipes.value)
    TestCase.assertTrue(searchViewModel.filteredRecipes.value.first().recipeId == recipeId)
  }

  @Test
  fun loadMoreProfiles_Success() {
    `when`(mockDocumentSnapshot.data).thenReturn(profileMap)
    searchViewModel.searchProfiles(queryUser)
    searchViewModel.loadMoreProfiles()
    Shadows.shadowOf(Looper.getMainLooper()).idle()

    println(searchViewModel.filteredProfiles.value)
    TestCase.assertTrue(searchViewModel.filteredProfiles.value.first().username == "user123")
  }
}
