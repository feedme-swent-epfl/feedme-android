package com.android.feedme.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.data.RecipeRepository
import com.android.feedme.model.viewmodel.GenerateViewModel
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows

@RunWith(RobolectricTestRunner::class)
class GenerateViewModelTest {
  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot

  private lateinit var recipeRepository: RecipeRepository
  private lateinit var profileRepository: ProfileRepository

  private lateinit var generateViewModel: GenerateViewModel

  @Mock private lateinit var mockQuery: Query
  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot

  @Mock private lateinit var mockContext: Context
  @Mock private lateinit var mockConnectivityManager: ConnectivityManager
  @Mock private lateinit var mockNetwork: Network
  @Mock private lateinit var mockNetworkCapabilities: NetworkCapabilities

  private val ingredientIds = listOf("flourId", "sugarId")
  private val recipeMap1: Map<String, Any> =
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

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }
    RecipeRepository.initialize(mockFirestore)
    ProfileRepository.initialize(mockFirestore)
    recipeRepository = RecipeRepository(mockFirestore)
    profileRepository = ProfileRepository(mockFirestore)

    `when`(mockFirestore.collection(recipeRepository.collectionPath))
        .thenReturn(mockCollectionReference)

    `when`(mockCollectionReference.document(Mockito.anyString())).thenReturn(mockDocumentReference)
    `when`(mockDocumentSnapshot.data).thenReturn(recipeMap1)
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))

    `when`(mockCollectionReference.whereArrayContainsAny("ingredientIds", ingredientIds))
        .thenReturn(mockQuery)
    `when`(mockQuery.limit(6)).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))

    // Mock the behavior of getSystemService to return mocked ConnectivityManager
    `when`(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE))
        .thenReturn(mockConnectivityManager)
    // Mock the behavior of activeNetwork
    `when`(mockConnectivityManager.activeNetwork).thenReturn(mockNetwork)
    // Mock the behavior of getNetworkCapabilities to return mocked NetworkCapabilities
    `when`(mockConnectivityManager.getNetworkCapabilities(mockNetwork))
        .thenReturn(mockNetworkCapabilities)
    // Mock the behavior of hasCapability to return true for internet capability
    `when`(mockNetworkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
        .thenReturn(true)

    generateViewModel = GenerateViewModel()
  }

  @Test
  fun testFullViewModelFunctionality() {
    val profile = Profile()
    generateViewModel.toggleStrictness(false)
    generateViewModel.fetchGeneratedRecipes(ingredientIds, profile, mockContext)
    generateViewModel.toggleStrictness(true)
    generateViewModel.fetchGeneratedRecipes(ingredientIds, profile, mockContext)

    Shadows.shadowOf(Looper.getMainLooper()).idle()
  }

  @Test
  fun testFetchGeneratedRecipes_Offline() {
    val profile = Profile()
    generateViewModel.toggleStrictness(true)
    generateViewModel.fetchGeneratedRecipes(ingredientIds, profile)
    generateViewModel.toggleStrictness(false)
    generateViewModel.fetchGeneratedRecipes(ingredientIds, profile)

    Shadows.shadowOf(Looper.getMainLooper()).idle()
  }
}
