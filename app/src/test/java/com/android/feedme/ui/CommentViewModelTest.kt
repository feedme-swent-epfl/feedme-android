package com.android.feedme.model.viewmodel

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.android.feedme.model.data.Comment
import com.android.feedme.model.data.CommentRepository
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.time.Instant
import java.util.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class CommentViewModelTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore

  @Mock private lateinit var mockDocumentReference: DocumentReference

  @Mock private lateinit var mockCollectionReference: CollectionReference

  private lateinit var viewModel: CommentViewModel

  @Before
  fun setUp() {
    // Initialize Mockito annotations
    MockitoAnnotations.openMocks(this)

    // Initialize Firebase if necessary
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    // Initialize the repository with the mocked Firestore instance
    CommentRepository.initialize(mockFirestore)
    `when`(mockFirestore.collection("comments")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.id).thenReturn("testCommentId")
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null))

    // Initialize the ViewModel
    viewModel = CommentViewModel()
  }

  @Test
  fun selectComment_setsComment() {
    val comment = createTestComment()
    viewModel.selectComment(comment)

    val result = viewModel.comment.value
    assertEquals(comment, result)
  }

  @Test
  fun addComment_success() {
    val comment = createTestComment()
    comment.commentId = "ajjaha"

    var bol = false
    viewModel.addComment(comment) { bol = true }

    // Ensure all asynchronous operations complete
    shadowOf(Looper.getMainLooper()).idle()

    val result = viewModel.comment.value
    assertEquals(comment, result)
    assertNotEquals(comment.commentId, "ajjaha")
    assertTrue(bol)
  }

  @Test
  fun addComment_withEmptyCommentId() {
    val comment = createTestComment()
    comment.commentId = ""

    var bol = false
    viewModel.addComment(comment) { bol = true }

    // Ensure all asynchronous operations complete
    shadowOf(Looper.getMainLooper()).idle()

    val result = viewModel.comment.value
    assertEquals(comment, result)
    assertNotEquals(comment.commentId, "")
    assertTrue(bol)
  }

  private fun createTestComment(): Comment {
    return Comment(
        "authorId",
        "recipeId",
        "photoURL",
        "hehehe",
        120.0,
        "Title",
        "Content",
        Date.from(Instant.now()))
  }
}
