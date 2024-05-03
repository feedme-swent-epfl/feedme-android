package com.android.feedme.ui

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.RecipeRepository
import com.android.feedme.model.data.Step
import com.android.feedme.model.viewmodel.AuthViewModel
import com.android.feedme.model.viewmodel.LandingPageViewModel
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.anyString
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

    private lateinit var recipeRepository: RecipeRepository

    private lateinit var landingViewModel: LandingPageViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
            FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
        }
        RecipeRepository.initialize(mockFirestore)

        recipeRepository = RecipeRepository.instance

        `when`(mockFirestore.collection("recipes")).thenReturn(mockCollectionReference)
        `when`(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference)

        landingViewModel = LandingPageViewModel()
    }

    @Test
    fun getProfile_Success() {
        val profileId = "1"
        var expectedRecipe = Recipe(
            recipeId = "lasagna1",
            title = "Tasty Lasagna",
            description =
            "Description of the recipe, writing a longer one to see if it fills up the whole space available. Still writing with no particular aim lol",
            ingredients =
            listOf(),
            steps =
            listOf(),
            tags = listOf("Meat"),
            time = 45.0,
            rating = 4.5,
            userid = "username",
            difficulty = "Intermediate",
            "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.mamablip.com%2Fstorage%2FLasagna%2520with%2520Meat%2520and%2520Tomato%2520Sauce_3481612355355.jpg&f=1&nofb=1&ipt=8e887ba99ce20a85fb867dabbe0206c1146ebf2f13548b5653a2778e3ea18c54&ipo=images")

        `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
        `when`(mockDocumentSnapshot.toObject(Recipe::class.java)).thenReturn(expectedRecipe)

        landingViewModel.fetchRecipe("lasagna1")
        shadowOf(Looper.getMainLooper()).idle()

        assertTrue(landingViewModel.recipes.value == listOf(expectedRecipe))
    }
}
