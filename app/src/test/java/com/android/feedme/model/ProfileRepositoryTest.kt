package com.android.feedme.model.data

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.*
import junit.framework.TestCase.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
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
}
