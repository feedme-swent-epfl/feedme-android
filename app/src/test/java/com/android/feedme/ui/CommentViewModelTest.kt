package com.android.feedme.model.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.test.core.app.ApplicationProvider
import com.android.feedme.model.data.Comment
import com.android.feedme.model.data.CommentRepository
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.data.RecipeRepository
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.*
import java.util.*
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
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

  @Mock private lateinit var mockDocumentSnapshot1: DocumentSnapshot

  @Mock private lateinit var mockDocumentSnapshot2: DocumentSnapshot

  @Mock private lateinit var mockProfileDocumentSnapshot: DocumentSnapshot

  @Mock private lateinit var mockContext: Context

  @Mock private lateinit var mockConnectivityManager: ConnectivityManager

  @Mock private lateinit var mockNetwork: Network

  @Mock private lateinit var mockNetworkCapabilities: NetworkCapabilities

  private lateinit var commentRepository: CommentRepository
  private lateinit var recipeRepository: RecipeRepository
  private lateinit var profileRepository: ProfileRepository

  private lateinit var viewModel: CommentViewModel

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    // Mock Firestore collections
    `when`(mockFirestore.collection("comments")).thenReturn(mockCommentCollection)
    `when`(mockFirestore.collection("recipes")).thenReturn(mockRecipeCollection)
    `when`(mockFirestore.collection("profiles")).thenReturn(mockProfileCollection)

    // Mock DocumentReferences
    `when`(mockCommentCollection.document(anyString())).thenReturn(mockCommentDocumentReference)
    `when`(mockRecipeCollection.document(anyString())).thenReturn(mockRecipeDocumentReference)
    `when`(mockProfileCollection.document(anyString())).thenReturn(mockProfileDocumentReference)
    `when`(mockCommentCollection.document()).thenReturn(mockCommentDocumentReference)

    // Ensure the mock DocumentReference has a valid ID
    `when`(mockCommentDocumentReference.id).thenReturn("testCommentId")

    // Mock Firestore transaction
    `when`(mockFirestore.runTransaction<Any>(any())).thenAnswer { invocation ->
      val transactionFunction = invocation.arguments[0] as Transaction.Function<*>
      transactionFunction.apply(mockTransaction)
      Tasks.forResult(null)
    }

    // Mock ConnectivityManager behavior
    `when`(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE))
        .thenReturn(mockConnectivityManager)
    `when`(mockConnectivityManager.activeNetwork).thenReturn(mockNetwork)
    `when`(mockConnectivityManager.getNetworkCapabilities(mockNetwork))
        .thenReturn(mockNetworkCapabilities)
    `when`(mockNetworkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
        .thenReturn(true)

    // Initialize repositories
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

  @Test
  fun addComment_success() = runTest {
    val comment =
        Comment(
            commentId = "testCommentId",
            userId = "testUserId",
            recipeId = "testRecipeId",
            photoURL = "photoURL",
            rating = 5.0,
            title = "title",
            content = "content",
            creationDate = Date())

    // Mock successful Firestore operations
    `when`(mockCommentDocumentReference.set(any())).thenReturn(Tasks.forResult(null))
    `when`(mockRecipeDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot1))
    `when`(mockRecipeDocumentReference.update(anyString(), any())).thenReturn(Tasks.forResult(null))
    `when`(mockProfileDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot2))
    `when`(mockProfileDocumentReference.update(anyString(), any()))
        .thenReturn(Tasks.forResult(null))

    // Mock DocumentSnapshot data for recipe and profile
    `when`(mockDocumentSnapshot1.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot1.data).thenReturn(mapOf("comments" to mutableListOf<String>()))
    `when`(mockDocumentSnapshot2.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot2.data).thenReturn(mapOf("commentList" to mutableListOf<String>()))

    // Add the comment
    viewModel.addComment(comment)

    // Advance coroutine execution
    advanceUntilIdle()

    // Retrieve and assert the stored comment
    val storedComment = viewModel.comment.first()
    assertNull(storedComment)
  }

  @Test
  fun addComment_failureAtCommentRepository() = runTest {
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

    // Simulate failure in adding comment
    `when`(mockCommentDocumentReference.set(any()))
        .thenReturn(Tasks.forException(Exception("Failed to add comment")))

    viewModel.addComment(comment)

    advanceUntilIdle()

    assert(viewModel.comment.first() == null)
  }

  @Test
  fun addComment_failureAtRecipeRepository() = runTest {
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

    // Simulate success in adding comment
    `when`(mockCommentDocumentReference.set(any())).thenReturn(Tasks.forResult(null))

    // Simulate failure in fetching recipe document
    `when`(mockRecipeDocumentReference.get())
        .thenReturn(Tasks.forException(Exception("Failed to add comment to recipe")))

    viewModel.addComment(comment)

    advanceUntilIdle()

    assert(viewModel.comment.first() == null)
  }

  @Test
  fun addComment_failureAtProfileRepository() = runTest {
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

    // Simulate success in adding comment
    `when`(mockCommentDocumentReference.set(any())).thenReturn(Tasks.forResult(null))
    `when`(mockRecipeDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot1))

    // Simulate failure in fetching profile document
    `when`(mockProfileDocumentReference.get())
        .thenReturn(Tasks.forException(Exception("Failed to add comment to profile")))

    viewModel.addComment(comment)

    advanceUntilIdle()

    assert(viewModel.comment.first() == null)
  }
}
