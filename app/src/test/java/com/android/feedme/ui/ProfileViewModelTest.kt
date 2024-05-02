package com.android.feedme.model

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Transaction
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.anyString
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class ProfileViewModelTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore

  @Mock private lateinit var mockDocumentReference: DocumentReference

  @Mock private lateinit var mockCollectionReference: CollectionReference

  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot

  private lateinit var profileRepository: ProfileRepository

  private lateinit var profileViewModel: ProfileViewModel
  @Mock private lateinit var mockQuery: Query
  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }
    ProfileRepository.initialize(mockFirestore)

    profileRepository = ProfileRepository.instance

    `when`(mockFirestore.collection("profiles")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference)

    `when`(mockCollectionReference.whereIn(anyString(), anyList())).thenReturn(mockQuery)

    `when`(mockQuery.get())
        .thenReturn(Tasks.forResult(mockQuerySnapshot)) // Ensure this returns a valid Task

    // You might need to mock what happens when the QuerySnapshot is processed
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.toObject(Profile::class.java))
        .thenReturn(Profile("1"), Profile("2")) // Example
    profileViewModel = ProfileViewModel()
  }

  @Test
  fun updateCurrentUserProfile_Success() {
    val profile = Profile("1")
    profileViewModel.updateCurrentUserProfile(profile)
    assertEquals("1", profileViewModel.currentUserId)
  }

  @Test
  fun setViewingProfile_Success() {
    val profile = Profile("1")
    profileViewModel.setViewingProfile(profile)
    assertEquals("1", profileViewModel.viewingUserId)
  }

  @Test
  fun fetchCurrentUserProfile_Success() {
    val profile = Profile("1")
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.toObject(Profile::class.java)).thenReturn(profile)

    profileViewModel.currentUserId = "1"
    profileViewModel.fetchCurrentUserProfile()
    shadowOf(Looper.getMainLooper()).idle()

    assertEquals("1", profileViewModel.currentUserProfile.value!!.id)
  }

  @Test
  fun fetchCurrentUserProfile_Failure() {
    `when`(mockDocumentReference.get())
        .thenReturn(Tasks.forException(Exception("Failed to fetch profile")))
    profileViewModel.currentUserId = "1"
    profileViewModel.fetchCurrentUserProfile()
    shadowOf(Looper.getMainLooper()).idle()
    assertTrue(profileViewModel.errorMessages.value!!.contains("Failed to fetch profile"))
  }

  @Test
  fun followUser_Success() {
    val currentUser = Profile("1")
    val targetUser = Profile("2")
    profileViewModel.currentUserId = "1"

    val mockTransaction = Mockito.mock(Transaction::class.java)

    val currentUserRef = Mockito.mock(DocumentReference::class.java)
    val targetUserRef = Mockito.mock(DocumentReference::class.java)

    val currentUserSnapshot = Mockito.mock(DocumentSnapshot::class.java)
    val targetUserSnapshot = Mockito.mock(DocumentSnapshot::class.java)

    // Setup document references
    `when`(mockFirestore.collection("profiles")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(currentUser.id)).thenReturn(currentUserRef)
    `when`(mockCollectionReference.document(targetUser.id)).thenReturn(targetUserRef)

    // Setup snapshot return from the transaction
    // Setup direct returns for document snapshots from the transaction
    `when`(mockTransaction.get(currentUserRef)).thenReturn(currentUserSnapshot)
    `when`(mockTransaction.get(targetUserRef)).thenReturn(targetUserSnapshot)

    `when`(mockFirestore.runTransaction<Any>(any())).thenAnswer { invocation ->
      val transactionFunction = invocation.arguments[0] as Transaction.Function<*>
      transactionFunction.apply(mockTransaction)
      Tasks.forResult(Pair(currentUser, targetUser)) // Mock successful transaction
    }

    profileViewModel.updateCurrentUserProfile(currentUser)
    profileViewModel.setViewingProfile(targetUser)

    profileViewModel.followUser(targetUser)
    shadowOf(Looper.getMainLooper()).idle()

    assertEquals("2", profileViewModel.currentUserFollowing.value.first().id)
    assertEquals("1", profileViewModel.viewingUserFollowers.value.first().id)
  }

  @Test
  fun unfollowUser_Success() {
    val currentUser = Profile("1", following = listOf("2"))
    val targetUser = Profile("2", followers = listOf("1"))
    profileViewModel.currentUserId = "1"

    val mockTransaction = Mockito.mock(Transaction::class.java)
    val currentUserRef = Mockito.mock(DocumentReference::class.java)
    val targetUserRef = Mockito.mock(DocumentReference::class.java)
    val currentUserSnapshot = Mockito.mock(DocumentSnapshot::class.java)
    val targetUserSnapshot = Mockito.mock(DocumentSnapshot::class.java)

    // Setup document references
    `when`(mockFirestore.collection("profiles")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(currentUser.id)).thenReturn(currentUserRef)
    `when`(mockCollectionReference.document(targetUser.id)).thenReturn(targetUserRef)

    `when`(mockTransaction.get(currentUserRef)).thenReturn(currentUserSnapshot)
    `when`(mockTransaction.get(targetUserRef)).thenReturn(targetUserSnapshot)

    `when`(mockFirestore.runTransaction<Any>(any())).thenAnswer { invocation ->
      val transactionFunction = invocation.arguments[0] as Transaction.Function<*>
      transactionFunction.apply(mockTransaction)
      Tasks.forResult(Pair(currentUser, targetUser)) // Mock successful transaction
    }

    profileViewModel.updateCurrentUserProfile(currentUser)
    profileViewModel.setViewingProfile(targetUser)

    profileViewModel.unfollowUser(targetUser)
    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(profileViewModel.currentUserFollowing.value.none { it.id == targetUser.id })
  }

  @Test
  fun unfollowUser_Failure() {
    val targetUser = Profile("2")
    profileViewModel.currentUserId = "1"

    `when`(mockFirestore.runTransaction<Any>(any()))
        .thenReturn(Tasks.forException(Exception("Transaction failed")))

    profileViewModel.unfollowUser(targetUser)
    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(profileViewModel.errorMessages.value!!.contains("Failed to unfollow user"))
  }

  @Test
  fun followUser_Failure() {
    val targetUser = Profile("2")
    profileViewModel.currentUserId = "1"

    `when`(mockFirestore.runTransaction<Any>(any()))
        .thenReturn(Tasks.forException(Exception("Transaction failed")))

    profileViewModel.followUser(targetUser)
    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(profileViewModel.errorMessages.value!!.contains("Failed to start following user"))
  }

  @Test
  fun removeFollower_Success() {
    val currentUser = Profile("1", followers = listOf("3")) // Assume '1' is followed by '3'
    val follower = Profile("3", following = listOf("1")) // Assume '3' is a follower of '1'
    profileViewModel.currentUserId = "1"
    profileViewModel.setViewingProfile(follower)

    val mockTransaction = Mockito.mock(Transaction::class.java)
    val userRef = Mockito.mock(DocumentReference::class.java)
    val followerRef = Mockito.mock(DocumentReference::class.java)
    val userSnapshot = Mockito.mock(DocumentSnapshot::class.java)
    val followerSnapshot = Mockito.mock(DocumentSnapshot::class.java)

    // Setup document references
    `when`(mockFirestore.collection("profiles")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(currentUser.id)).thenReturn(userRef)
    `when`(mockCollectionReference.document(follower.id)).thenReturn(followerRef)

    `when`(mockTransaction.get(userRef)).thenReturn(userSnapshot)
    `when`(mockTransaction.get(followerRef)).thenReturn(followerSnapshot)

    `when`(mockFirestore.runTransaction<Any>(any())).thenAnswer { invocation ->
      val transactionFunction = invocation.arguments[0] as Transaction.Function<*>
      transactionFunction.apply(mockTransaction)
      Tasks.forResult(Pair(currentUser, follower)) // Mock successful transaction
    }

    profileViewModel.updateCurrentUserProfile(currentUser)

    profileViewModel.removeFollower(follower)
    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(profileViewModel.currentUserFollowers.value.none { it.id == follower.id })
  }

  @Test
  fun removeFollower_Failure() {
    val follower = Profile("3")
    profileViewModel.currentUserId = "1"

    `when`(mockFirestore.runTransaction<Any>(any()))
        .thenReturn(Tasks.forException(Exception("Transaction failed")))

    profileViewModel.removeFollower(follower)
    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(profileViewModel.errorMessages.value!!.contains("Failed to remove follower"))
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

    profileViewModel.fetchProfile("1")
    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(profileViewModel.viewingUserProfile.value!!.email == expectedProfile.email)
  }

  @Test
  fun isViewingProfile_ViewingUserIdNotNull_ReturnsTrue() {
    profileViewModel.currentUserId = "1"
    profileViewModel.viewingUserId = "2"
    assertTrue(profileViewModel.isViewingProfile())
  }

  @Test
  fun isViewingProfile_ViewingUserIdNull_ReturnsFalse() {
    profileViewModel.currentUserId = "1"
    profileViewModel.viewingUserId = null
    assertFalse(profileViewModel.isViewingProfile())
  }

  @Test
  fun isViewingProfile_ViewingUserIdNotNull_ThenNull_ReturnsFalse() {
    profileViewModel.currentUserId = "1"
    profileViewModel.viewingUserId = "2"
    profileViewModel.removeViewingProfile()
    assertFalse(profileViewModel.isViewingProfile())
  }

  @Test
  fun profileToShow_ViewingProfile_ReturnsViewingProfile() {
    profileViewModel.currentUserId = "1"
    profileViewModel.viewingUserId = null
    profileViewModel.setViewingProfile(Profile("2", "John", "blabla", "john@example.com"))
    val profile = profileViewModel.profileToShow()
    assertEquals("2", profile.id)
    assertEquals("John", profile.name)
    assertEquals("blabla", profile.username)
    assertEquals("john@example.com", profile.email)
  }

  @Test
  fun isViewingThrow() {
    profileViewModel.currentUserId = "1"
    profileViewModel.viewingUserId = null
    // catch exception of isViewingProfile
    try {
      profileViewModel.isViewingProfile()
    } catch (e: Exception) {
      assertTrue(e is IllegalStateException)
    }
  }
}
