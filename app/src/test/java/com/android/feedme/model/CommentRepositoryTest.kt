package com.android.feedme.model

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.android.feedme.model.data.Comment
import com.android.feedme.model.data.CommentRepository
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.time.Instant
import java.util.Date
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.fail
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class CommentRepositoryTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore

  @Mock private lateinit var mockDocumentReference: DocumentReference

  @Mock private lateinit var mockCollectionReference: CollectionReference

  private lateinit var commentRepository: CommentRepository

  @Before
  fun setUp() {
    // Initialize Mockito annotations
    MockitoAnnotations.openMocks(this)

    // Initialize Firebase only if absolutely necessary for the test
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    // Initialize your Database class with the mocked FirebaseFirestore instance
    // Make sure your Database class accepts FirebaseFirestore as a constructor parameter
    commentRepository = CommentRepository(mockFirestore)

    // Setup your mocks as needed
    `when`(mockFirestore.collection("comments")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference)
  }

  @Test
  fun addComment_Success() {
    // Setup
    val comment =
        Comment(
            "authorId",
            "recipeId",
            "photoURL",
            5.0,
            120.0,
            "Title",
            "Content",
            Date.from(Instant.now()))
    `when`(mockFirestore.collection("comments")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(comment.authorId)).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null)) // Simulate success

    // Execute
    var successCalled = false
    commentRepository.addComment(comment, { successCalled = true }, {})

    // This ensures all asynchronous operations complete
    shadowOf(Looper.getMainLooper()).idle()

    // Verify
    verify(mockDocumentReference).set(any(Comment::class.java))
    assertTrue("Success callback was not called", successCalled)
  }

  @Test
  fun getComment_Success() {
    // Setup
    val commentId = "testCommentId"
    val expectedComment =
        Comment(
            "authorId",
            "recipeId",
            "photoURL",
            5.0,
            120.0,
            "Title",
            "Content",
            Date.from(Instant.now()))
    val mockSnapshot = mock(DocumentSnapshot::class.java)
    `when`(mockFirestore.collection("comments")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(commentId)).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockSnapshot))
    `when`(mockSnapshot.toObject(Comment::class.java)).thenReturn(expectedComment)

    // Execute & Verify
    commentRepository.getComment(
        commentId,
        { comment -> assertEquals(expectedComment, comment) },
        { fail("Failure callback was called") })
  }

  @Test
  fun addComment_Failure() {
    val comment =
        Comment(
            "authorId",
            "recipeId",
            "photoURL",
            5.0,
            120.0,
            "Title",
            "Content",
            Date.from(Instant.now()))
    val exception = Exception("Firestore set operation failed")
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forException(exception))

    var failureCalled = false
    commentRepository.addComment(
        comment,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { failureCalled = true })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue("Failure callback was not called", failureCalled)
  }

  @Test
  fun getComment_Failure() {
    val commentId = "nonexistentId"
    val exception = Exception("Firestore get operation failed")
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forException(exception))

    var failureCalled = false
    commentRepository.getComment(
        commentId,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { failureCalled = true })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue("Failure callback was not called", failureCalled)
  }

  @Test
  fun testSingletonInitialization() {
    val mockFirestore = mock(FirebaseFirestore::class.java)
    CommentRepository.initialize(mockFirestore)

    assertNotNull("Singleton instance should be initialized", CommentRepository.instance)
  }
}
