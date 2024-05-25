package com.android.feedme.ui

import androidx.test.core.app.ApplicationProvider
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.RecipeRepository
import com.android.feedme.model.data.Step
import com.android.feedme.model.viewmodel.RecipeViewModel
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RecipeViewModelTest {
  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot
  @Mock private lateinit var mockIngredientsCollectionReference: CollectionReference
  @Mock private lateinit var mockIngredientDocumentSnapshot: DocumentSnapshot

  @Mock private lateinit var recipeViewModel: RecipeViewModel

  private lateinit var recipeRepository: RecipeRepository
  private lateinit var profileRepository: ProfileRepository

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

    Mockito.`when`(mockFirestore.collection(recipeRepository.collectionPath))
        .thenReturn(mockCollectionReference)
    Mockito.`when`(mockFirestore.collection("ingredients"))
        .thenReturn(mockIngredientsCollectionReference)

    Mockito.`when`(mockCollectionReference.document(Mockito.anyString()))
        .thenReturn(mockDocumentReference)
    Mockito.`when`(mockIngredientsCollectionReference.document(Mockito.anyString()))
        .thenReturn(mockDocumentReference)

    Mockito.`when`(mockDocumentReference.get())
        .thenReturn(Tasks.forResult(mockIngredientDocumentSnapshot))

    // Here's the critical part: ensure a Task<Void> is returned
    Mockito.`when`(mockDocumentReference.set(Mockito.any())).thenReturn(Tasks.forResult(null))

    Mockito.`when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    Mockito.`when`(mockDocumentSnapshot.exists()).thenReturn(true)

    recipeViewModel = RecipeViewModel()
  }

  @Test
  fun validateRecipe_Success() {
    assert(
        recipeViewModel.validateRecipe(
            "recipeTitle",
            "recipeName",
            listOf(
                IngredientMetaData(
                    200.0, MeasureUnit.G, Ingredient("Ingredient 1", "ID", false, false))),
            listOf(
                Step(1, "test", "test"),
            ),
            "userId",
            ""))
  }

  @Test
  fun validateRecipe_Failure() {
    assert(
        !recipeViewModel.validateRecipe(
            "",
            "recipeName",
            listOf(
                IngredientMetaData(
                    0.0, MeasureUnit.TEASPOON, Ingredient("test", "test", false, false))),
            listOf(
                Step(1, "test", "test"),
            ),
            "userId",
            ""))

    assert(
        !recipeViewModel.validateRecipe(
            "recipeTitle",
            "recipeName",
            listOf(
                IngredientMetaData(
                    0.0, MeasureUnit.TEASPOON, Ingredient("test", "test", false, false))),
            emptyList(),
            "userId",
            ""))

    assert(
        !recipeViewModel.validateRecipe(
            "recipeTitle",
            "recipeName",
            emptyList(),
            listOf(
                Step(1, "test", "test"),
            ),
            "userId",
            ""))

    assert(
        !recipeViewModel.validateRecipe(
            "recipeTitle",
            "recipeName",
            listOf(
                IngredientMetaData(
                    0.0, MeasureUnit.TEASPOON, Ingredient("test", "test", false, false))),
            listOf(
                Step(1, "test", "test"),
            ),
            "",
            ""))
  }

  @Test
  fun setRecipe_Offline() {
    recipeViewModel.setRecipe(
        Recipe(
            "DEFAULT_ID",
            "recipeTitle",
            "recipeName",
            listOf(
                IngredientMetaData(
                    200.0, MeasureUnit.G, Ingredient("Ingredient 1", "ID", false, false))),
            listOf(
                Step(1, "test", "test"),
            ),
            emptyList(),
            0.0,
            "userId",
            ""))
    assert(recipeViewModel.recipe.value == null)
  }
}
