package com.android.feedme.model

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.android.feedme.model.data.Comment
import com.android.feedme.model.data.CommentRepository
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.*
import java.time.Instant
import java.util.*
import junit.framework.TestCase.*
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

    // Initialize Firebase if necessary
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    // Initialize the repository with the mocked Firestore instance
    commentRepository = CommentRepository(mockFirestore)

    // Setup your mocks as needed
    `when`(mockFirestore.collection("comments")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.id).thenReturn("testCommentId") // Mock the ID property
  }

  @Test
  fun addComment_Success() {
    // Setup
    val comment = createTestComment()
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null)) // Simulate success

    // Execute
    var successCalled = false
    commentRepository.addComment(comment, null, { successCalled = true }, {})

    // Ensure all asynchronous operations complete
    shadowOf(Looper.getMainLooper()).idle()

    // Verify
    verify(mockDocumentReference).set(any(Comment::class.java))
    assertTrue("Success callback was not called", successCalled)
  }

  @Test
  fun getComment_Success() {
    // Setup
    val commentId = "testCommentId"
    val expectedComment = createTestComment()
    val mockSnapshot = mock(DocumentSnapshot::class.java)
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
    // Setup
    val comment = createTestComment()
    val exception = Exception("Firestore set operation failed")
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forException(exception))

    // Execute
    var failureCalled = false
    commentRepository.addComment(
        comment,
        null,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { failureCalled = true })

    // Ensure all asynchronous operations complete
    shadowOf(Looper.getMainLooper()).idle()

    // Verify
    assertTrue("Failure callback was not called", failureCalled)
  }

  @Test
  fun getComment_Failure() {
    // Setup
    val commentId = "nonexistentId"
    val exception = Exception("Firestore get operation failed")
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forException(exception))

    // Execute
    var failureCalled = false
    commentRepository.getComment(
        commentId,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { failureCalled = true })

    // Ensure all asynchronous operations complete
    shadowOf(Looper.getMainLooper()).idle()

    // Verify
    assertTrue("Failure callback was not called", failureCalled)
  }

  @Test
  fun testSingletonInitialization() {
    val mockFirestore = mock(FirebaseFirestore::class.java)
    CommentRepository.initialize(mockFirestore)

    assertNotNull("Singleton instance should be initialized", CommentRepository.instance)
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
