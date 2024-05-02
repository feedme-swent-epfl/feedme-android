package com.android.feedme.model

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.ProfileRepository
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class ProfileRepositoryTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore

  @Mock private lateinit var mockDocumentReference: DocumentReference

  @Mock private lateinit var mockCollectionReference: CollectionReference

  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot

  private lateinit var profileRepository: ProfileRepository

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    profileRepository = ProfileRepository(mockFirestore)

    `when`(mockFirestore.collection("profiles")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference)
  }

  @Test
  fun addProfile_Success() {
    val profile =
        Profile(
            "1",
            "John Doe",
            "johndoe",
            "john@example.com",
            "A short bio",
            "http://example.com/image.png",
            listOf(),
            listOf(),
            listOf(),
            listOf(),
            listOf())
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null))

    var successCalled = false
    profileRepository.addProfile(profile, { successCalled = true }, {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).set(profile)
    assertTrue("Success callback was not called", successCalled)
  }

  @Test
  fun getProfile_Success() {
    val profileId = "1"
    val expectedProfile =
        Profile(
            profileId,
            "John Doe",
            "johndoe",
            "john@example.com",
            "A short bio",
            "http://example.com/image.png",
            listOf(),
            listOf(),
            listOf(),
            listOf(),
            listOf())
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.toObject(Profile::class.java)).thenReturn(expectedProfile)

    profileRepository.getProfile(
        profileId,
        { profile -> assertEquals(expectedProfile, profile) },
        { fail("Failure callback was called") })

    shadowOf(Looper.getMainLooper()).idle()
  }

  @Test
  fun addProfile_Failure() {
    val profile =
        Profile(
            "1",
            "John Doe",
            "johndoe",
            "john@example.com",
            "A short bio",
            "http://example.com/image.png",
            listOf(),
            listOf(),
            listOf(),
            listOf(),
            listOf())
    val exception = Exception("Firestore failure")
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forException(exception))

    var failureCalled = false
    profileRepository.addProfile(
        profile,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { failureCalled = true })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue("Failure callback was not called", failureCalled)
  }

  @Test
  fun getProfile_Failure() {
    val profileId = "nonexistent"
    val exception = Exception("Firestore failure")
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forException(exception))

    var failureCalled = false
    profileRepository.getProfile(
        profileId,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { failureCalled = true })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue("Failure callback was not called", failureCalled)
  }

  @Test
  fun testSingletonInitialization() {
    val mockFirestore = mock(FirebaseFirestore::class.java)
    ProfileRepository.initialize(mockFirestore)

    assertNotNull("Singleton instance should be initialized", ProfileRepository.instance)
  }

  @Test
  fun followUser_Success() {
    val currentUserID = "1"
    val targetUserID = "2"
    val mockTransaction = mock(Transaction::class.java)

    val currentUserRef = mock(DocumentReference::class.java)
    val targetUserRef = mock(DocumentReference::class.java)

    val currentUserSnapshot = mock(DocumentSnapshot::class.java)
    val targetUserSnapshot = mock(DocumentSnapshot::class.java)

    // Setup document references
    `when`(mockFirestore.collection("profiles")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(currentUserID)).thenReturn(currentUserRef)
    `when`(mockCollectionReference.document(targetUserID)).thenReturn(targetUserRef)

    // Setup snapshot return from the transaction
    // Setup direct returns for document snapshots from the transaction
    `when`(mockTransaction.get(currentUserRef)).thenReturn(currentUserSnapshot)
    `when`(mockTransaction.get(targetUserRef)).thenReturn(targetUserSnapshot)

    val currentUser = Profile(currentUserID)
    val targetUser = Profile(targetUserID)
    `when`(currentUserSnapshot.toObject(Profile::class.java)).thenReturn(currentUser)
    `when`(targetUserSnapshot.toObject(Profile::class.java)).thenReturn(targetUser)

    // Mock the transaction function to return a Task
    `when`(mockFirestore.runTransaction<Any>(any())).thenAnswer { invocation ->
      val transactionFunction = invocation.arguments[0] as Transaction.Function<*>
      val resultPair = transactionFunction.apply(mockTransaction)
      Tasks.forResult(resultPair)
    }

    var successCalled = false
    profileRepository.followUser(
        currentUserID,
        targetUserID,
        { user1, user2 -> successCalled = true },
        { fail("Failure should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockTransaction).get(currentUserRef)
    verify(mockTransaction).get(targetUserRef)
    assertTrue("Success callback was not called", successCalled)
  }

  @Test
  fun unfollowUser_Success() {
    val currentUserID = "1"
    val targetUserID = "2"
    val mockTransaction = mock(Transaction::class.java)

    val currentUserRef = mock(DocumentReference::class.java)
    val targetUserRef = mock(DocumentReference::class.java)

    val currentUserSnapshot = mock(DocumentSnapshot::class.java)
    val targetUserSnapshot = mock(DocumentSnapshot::class.java)

    // Setup document references
    `when`(mockFirestore.collection("profiles")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(currentUserID)).thenReturn(currentUserRef)
    `when`(mockCollectionReference.document(targetUserID)).thenReturn(targetUserRef)

    // Setup direct returns for document snapshots from the transaction
    `when`(mockTransaction.get(currentUserRef)).thenReturn(currentUserSnapshot)
    `when`(mockTransaction.get(targetUserRef)).thenReturn(targetUserSnapshot)

    val currentUser =
        Profile(
            currentUserID,
            following = listOf("2")) // Populate with appropriate constructor arguments
    val targetUser =
        Profile(
            targetUserID,
            followers = listOf("1")) // Populate with appropriate constructor arguments
    `when`(currentUserSnapshot.toObject(Profile::class.java)).thenReturn(currentUser)
    `when`(targetUserSnapshot.toObject(Profile::class.java)).thenReturn(targetUser)

    // Mock the transaction function to execute and use a mock result
    `when`(mockFirestore.runTransaction<Any>(any())).thenAnswer { invocation ->
      val transactionFunction = invocation.arguments[0] as Transaction.Function<*>
      transactionFunction.apply(mockTransaction) // Simulates the transaction being executed
      Tasks.forResult(Pair(currentUser, targetUser)) // Correctly return a Task wrapping the Pair
    }

    var successCalled = false
    profileRepository.unfollowUser(
        currentUserID,
        targetUserID,
        { user1, user2 ->
          successCalled = true
          assertEquals("Following list should be empty", 0, user1.following.size)
          assertEquals("Followers list should be empty", 0, user2.followers.size)
        },
        { fail("Failure should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockTransaction).get(currentUserRef)
    verify(mockTransaction).get(targetUserRef)
    assertTrue("Success callback was not called", successCalled)
  }

    @Test
    fun deleteProfile_Success() {
        val id = "1"
        val mockDocumentReference = mock(DocumentReference::class.java)

        `when`(mockFirestore.collection("profiles")).thenReturn(mockCollectionReference)
        `when`(mockCollectionReference.document(id)).thenReturn(mockDocumentReference)
        `when`(mockDocumentReference.delete()).thenReturn(Tasks.forResult(null))

        var successCalled = false
        profileRepository.deleteProfile(id, { successCalled = true }, { fail("Failure should not be called") })

        shadowOf(Looper.getMainLooper()).idle()

        verify(mockDocumentReference).delete()
        assertTrue("Success callback was not called", successCalled)
    }

    @Test
    fun deleteProfile_Failure() {
        val id = "1"
        val exception = Exception("Firestore deletion failure")
        val mockDocumentReference = mock(DocumentReference::class.java)

        `when`(mockFirestore.collection("profiles")).thenReturn(mockCollectionReference)
        `when`(mockCollectionReference.document(id)).thenReturn(mockDocumentReference)
        `when`(mockDocumentReference.delete()).thenReturn(Tasks.forException(exception))

        var failureCalled = false
        profileRepository.deleteProfile(
            id,
            onSuccess = { fail("Success callback should not be called") },
            onFailure = { failureCalled = true }
        )

        shadowOf(Looper.getMainLooper()).idle()

        assertTrue("Failure callback was not called", failureCalled)
    }
}
