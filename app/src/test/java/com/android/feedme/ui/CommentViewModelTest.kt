package com.android.feedme.model.viewmodel

import com.android.feedme.model.data.Comment
import com.android.feedme.model.data.CommentRepository
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.data.RecipeRepository
import com.google.firebase.firestore.*
import java.util.Date
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class CommentViewModelTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore

  @Mock private lateinit var mockTransaction: Transaction

  @Mock private lateinit var mockCommentCollection: CollectionReference

  @Mock private lateinit var mockRecipeCollection: CollectionReference

  @Mock private lateinit var mockProfileCollection: CollectionReference

  @Mock private lateinit var mockCommentDocumentReference: DocumentReference

  @Mock private lateinit var mockRecipeDocumentReference: DocumentReference

  @Mock private lateinit var mockProfileDocumentReference: DocumentReference

  @Mock private lateinit var mockRecipeDocumentSnapshot: DocumentSnapshot

  @Mock private lateinit var mockProfileDocumentSnapshot: DocumentSnapshot

  private lateinit var commentRepository: CommentRepository
  private lateinit var recipeRepository: RecipeRepository
  private lateinit var profileRepository: ProfileRepository

  private lateinit var viewModel: CommentViewModel

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    // Mock the behavior of the Firestore collections
    `when`(mockFirestore.collection("comments")).thenReturn(mockCommentCollection)
    `when`(mockFirestore.collection("recipes")).thenReturn(mockRecipeCollection)
    `when`(mockFirestore.collection("profiles")).thenReturn(mockProfileCollection)

    // Mock the behavior of the DocumentReferences
    `when`(mockCommentCollection.document(anyString())).thenReturn(mockCommentDocumentReference)
    `when`(mockRecipeCollection.document(anyString())).thenReturn(mockRecipeDocumentReference)
    `when`(mockProfileCollection.document(anyString())).thenReturn(mockProfileDocumentReference)

    // Initialize the repositories with the mocked Firestore instance
    CommentRepository.initialize(mockFirestore)
    RecipeRepository.initialize(mockFirestore)
    ProfileRepository.initialize(mockFirestore)
    commentRepository = CommentRepository.instance
    recipeRepository = RecipeRepository.instance
    profileRepository = ProfileRepository.instance

    viewModel = CommentViewModel()
  }

  @Test
  fun selectComment_updatesCommentState() = runTest {
    val comment =
        Comment(
            commentId = "commentId",
            userId = "userId",
            recipeId = "recipeId",
            photoURL = "photoURL",
            rating = 5.0,
            title = "title",
            content = "content",
            creationDate = Date())

    viewModel.selectComment(comment)

    assert(viewModel.comment.first() == comment)
  }
}
