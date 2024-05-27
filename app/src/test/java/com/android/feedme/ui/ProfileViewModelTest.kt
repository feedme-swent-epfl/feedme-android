package com.android.feedme.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Looper
import android.widget.Toast
import androidx.test.core.app.ApplicationProvider
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.model.viewmodel.isNetworkAvailable
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
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
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
  @Mock private lateinit var mockQuery: Query
  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot
  @Mock private lateinit var mockTransaction: Transaction

  @Mock private lateinit var mockFirebaseApp: FirebaseApp
  @Mock private lateinit var mockContext: Context
  @Mock private lateinit var mockConnectivityManager: ConnectivityManager
  @Mock private lateinit var mockNetwork: Network
  @Mock private lateinit var mockNetworkCapabilities: NetworkCapabilities

  private lateinit var profileRepository: ProfileRepository
  private lateinit var profileViewModel: ProfileViewModel

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }
    // Mock FirebaseApp and application context
    `when`(mockFirestore.app).thenReturn(mockFirebaseApp)
    `when`(mockFirebaseApp.applicationContext)
        .thenReturn(ApplicationProvider.getApplicationContext())

    ProfileRepository.initialize(mockFirestore)

    profileRepository = ProfileRepository.instance

    `when`(mockFirestore.collection("profiles")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference)

    `when`(mockCollectionReference.whereIn(anyString(), anyList())).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.toObject(Profile::class.java))
        .thenReturn(Profile("1"), Profile("2"))

    `when`(mockFirestore.runTransaction<Any>(any())).thenReturn(Tasks.forResult(null))

    profileViewModel = ProfileViewModel()

    // Mock the behavior of getSystemService to return mocked ConnectivityManager
    `when`(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE))
        .thenReturn(mockConnectivityManager)
    // Mock the behavior of activeNetwork
    `when`(mockConnectivityManager.activeNetwork).thenReturn(mockNetwork)
    // Mock the behavior of getNetworkCapabilities to return mocked NetworkCapabilities
    `when`(mockConnectivityManager.getNetworkCapabilities(mockNetwork))
        .thenReturn(mockNetworkCapabilities)
    // Mock the behavior of hasCapability to return true for internet capability
    `when`(mockNetworkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
        .thenReturn(true)
    // Mock Toast to prevent actual Toast from showing in unit tests
    Mockito.mockStatic(Toast::class.java).use { mockedStatic ->
      mockedStatic
          .`when`<Any> { Toast.makeText(any(Context::class.java), anyString(), Mockito.anyInt()) }
          .thenReturn(mock(Toast::class.java))
    }
  }

  @Test
  fun testIsNetworkAvailable() {
    val result = isNetworkAvailable(mockContext)
    assertTrue(result)
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

    val mockTransaction = mock(Transaction::class.java)

    val currentUserRef = mock(DocumentReference::class.java)
    val targetUserRef = mock(DocumentReference::class.java)

    val currentUserSnapshot = mock(DocumentSnapshot::class.java)
    val targetUserSnapshot = mock(DocumentSnapshot::class.java)

    // Setup document references
    `when`(mockFirestore.collection("profiles")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(currentUser.id)).thenReturn(currentUserRef)
    `when`(mockCollectionReference.document(targetUser.id)).thenReturn(targetUserRef)

    // Setup snapshot return from the transaction
    `when`(mockTransaction.get(currentUserRef)).thenReturn(currentUserSnapshot)
    `when`(mockTransaction.get(targetUserRef)).thenReturn(targetUserSnapshot)

    `when`(mockFirestore.runTransaction<Any>(any())).thenAnswer { invocation ->
      val transactionFunction = invocation.arguments[0] as Transaction.Function<*>
      transactionFunction.apply(mockTransaction)
      Tasks.forResult(Pair(currentUser, targetUser))
    }

    profileViewModel.updateCurrentUserProfile(currentUser)
    profileViewModel.setViewingProfile(targetUser)

    profileViewModel.followUser(targetUser)
    shadowOf(Looper.getMainLooper()).idle()

    assertEquals("1", profileViewModel.viewingUserFollowers.value.first().id)
    assertEquals("2", profileViewModel.currentUserFollowing.value.first().id)
  }

  @Test
  fun unfollowUser_Success() {
    val currentUser = Profile("1", following = listOf("2"))
    val targetUser = Profile("2", followers = listOf("1"))
    profileViewModel.currentUserId = "1"

    val mockTransaction = mock(Transaction::class.java)
    val currentUserRef = mock(DocumentReference::class.java)
    val targetUserRef = mock(DocumentReference::class.java)
    val currentUserSnapshot = mock(DocumentSnapshot::class.java)
    val targetUserSnapshot = mock(DocumentSnapshot::class.java)

    // Setup document references
    `when`(mockFirestore.collection("profiles")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(currentUser.id)).thenReturn(currentUserRef)
    `when`(mockCollectionReference.document(targetUser.id)).thenReturn(targetUserRef)

    `when`(mockTransaction.get(currentUserRef)).thenReturn(currentUserSnapshot)
    `when`(mockTransaction.get(targetUserRef)).thenReturn(targetUserSnapshot)

    `when`(mockFirestore.runTransaction<Any>(any())).thenAnswer { invocation ->
      val transactionFunction = invocation.arguments[0] as Transaction.Function<*>
      transactionFunction.apply(mockTransaction)
      Tasks.forResult(Pair(currentUser, targetUser))
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
    val currentUser = Profile("1", followers = listOf("3"))
    val follower = Profile("3", following = listOf("1"))
    profileViewModel.currentUserId = "1"
    profileViewModel.setViewingProfile(follower)

    val mockTransaction = mock(Transaction::class.java)
    val userRef = mock(DocumentReference::class.java)
    val followerRef = mock(DocumentReference::class.java)
    val userSnapshot = mock(DocumentSnapshot::class.java)
    val followerSnapshot = mock(DocumentSnapshot::class.java)

    // Setup document references
    `when`(mockFirestore.collection("profiles")).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(currentUser.id)).thenReturn(userRef)
    `when`(mockCollectionReference.document(follower.id)).thenReturn(followerRef)

    `when`(mockTransaction.get(userRef)).thenReturn(userSnapshot)
    `when`(mockTransaction.get(followerRef)).thenReturn(followerSnapshot)

    `when`(mockFirestore.runTransaction<Any>(any())).thenAnswer { invocation ->
      val transactionFunction = invocation.arguments[0] as Transaction.Function<*>
      transactionFunction.apply(mockTransaction)
      Tasks.forResult(Pair(currentUser, follower))
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

  @Test(expected = Exception::class)
  fun `isViewingProfile throws Exception when currentUserId is null`() {
    profileViewModel.currentUserId = null
    profileViewModel.isViewingProfile()
  }

  @Test(expected = IllegalStateException::class)
  fun `deleteCurrentUserProfile throws Exception when currentUserId is null`() {
    profileViewModel.currentUserId = null
    profileViewModel.deleteCurrentUserProfile(mockContext, {}, {})
  }

  @Test(expected = Exception::class)
  fun `profileToShow throws Exception when both currentUserProfile and viewingUserProfile are null`() {
    profileViewModel.currentUserId = null
    profileViewModel.profileToShow()
  }

  @Test(expected = Exception::class)
  fun `deleteCurrentUserProfile throws Exception when currentUserId is DEFAULT_ID`() {
    profileViewModel.currentUserId = "DEFAULT_ID"
    profileViewModel.deleteCurrentUserProfile(mockContext, {}, { throw Exception() })
  }

  // New Tests for ProfileRepository and ProfileViewModel Methods

  @Test
  fun addSavedRecipe_Success() {
    val userId = "1"
    val recipe = "Recipe1"
    profileViewModel.currentUserId = userId

    `when`(mockFirestore.runTransaction<Any>(any())).thenAnswer { invocation ->
      val transactionFunction = invocation.arguments[0] as Transaction.Function<*>
      transactionFunction.apply(mockTransaction)
      Tasks.forResult(null)
    }
    `when`(mockTransaction.get(any(DocumentReference::class.java))).thenReturn(mockDocumentSnapshot)

    profileViewModel.addSavedRecipes(recipe, mockContext)
    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(profileViewModel.currentUserSavedRecipes.value!!.contains(recipe))
  }

  @Test
  fun addSavedRecipe_Failure() {
    val userId = "1"
    val recipe = "Recipe1"
    profileViewModel.currentUserId = userId

    `when`(mockFirestore.runTransaction<Any>(any()))
        .thenReturn(Tasks.forException(Exception("Transaction failed")))

    try {
      profileViewModel.addSavedRecipes(recipe, mockContext)
      shadowOf(Looper.getMainLooper()).idle()
    } catch (e: Exception) {
      assertTrue(e.message!!.contains("Can't add recipe to the database"))
    }
  }

  @Test
  fun removeSavedRecipe_Success() {
    val userId = "1"
    val recipe = "Recipe1"
    profileViewModel.currentUserId = userId

    `when`(mockFirestore.runTransaction<Any>(any())).thenAnswer { invocation ->
      val transactionFunction = invocation.arguments[0] as Transaction.Function<*>
      transactionFunction.apply(mockTransaction)
      Tasks.forResult(null)
    }
    `when`(mockTransaction.get(any(DocumentReference::class.java))).thenReturn(mockDocumentSnapshot)

    profileViewModel.addSavedRecipes(recipe, mockContext) // Add recipe first
    profileViewModel.removeSavedRecipes(recipe, mockContext)
    shadowOf(Looper.getMainLooper()).idle()

    assertFalse(profileViewModel.currentUserSavedRecipes.value!!.contains(recipe))
  }

  @Test
  fun removeSavedRecipe_Failure() {
    val userId = "1"
    val recipe = "Recipe1"
    profileViewModel.currentUserId = userId

    `when`(mockFirestore.runTransaction<Any>(any()))
        .thenReturn(Tasks.forException(Exception("Transaction failed")))

    try {
      profileViewModel.removeSavedRecipes(recipe)
      shadowOf(Looper.getMainLooper()).idle()
    } catch (e: Exception) {
      assertTrue(e.message!!.contains("Can't remove recipe from the database"))
    }
  }

  @Test
  fun savedRecipeExists_Success() {
    val userId = "1"
    val recipe = "Recipe1"
    profileViewModel.currentUserId = userId

    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.get("savedRecipes")).thenReturn(listOf(recipe))

    profileViewModel.savedRecipeExists(recipe, mockContext) { exists -> assertTrue(exists) }
  }

  @Test
  fun savedRecipeExists_Failure() {
    val userId = "1"
    val recipe = "Recipe1"
    profileViewModel.currentUserId = userId

    `when`(mockDocumentReference.get())
        .thenReturn(Tasks.forException(Exception("Transaction failed")))

    try {
      profileViewModel.savedRecipeExists(recipe, mockContext) { exists -> assertFalse(exists) }
    } catch (e: Exception) {
      assertTrue(e.message!!.contains("Can't check if recipe exists in the database"))
    }
  }

  @Test
  fun showDialog_Success() {
    val userId = "1"
    profileViewModel.currentUserId = userId

    `when`(mockFirestore.runTransaction<Any>(any())).thenAnswer { invocation ->
      val transactionFunction = invocation.arguments[0] as Transaction.Function<*>
      transactionFunction.apply(mockTransaction)
      Tasks.forResult(null)
    }
    `when`(mockTransaction.get(any(DocumentReference::class.java))).thenReturn(mockDocumentSnapshot)

    profileViewModel.setDialog(false)
    shadowOf(Looper.getMainLooper()).idle()

    if (isNetworkAvailable(mockContext)) {
      assertTrue(profileViewModel.showDialog.value)
    } else {
      assertFalse(profileViewModel.showDialog.value)
    }
  }

  @Test
  fun showDialog_Failure() {
    val userId = "1"
    profileViewModel.currentUserId = userId

    `when`(mockFirestore.runTransaction<Any>(any()))
        .thenReturn(Tasks.forException(Exception("Transaction failed")))

    try {
      profileViewModel.setDialog(false)
      shadowOf(Looper.getMainLooper()).idle()
    } catch (e: Exception) {
      assertTrue(e.message!!.contains("Can't set dialog in the database"))
    }
  }

  @Test
  fun setProfile_Offline() {
    profileViewModel.setProfile(Profile())
    shadowOf(Looper.getMainLooper()).idle()

    assertEquals(profileViewModel.currentUserProfile.value, null)
  }

  @Test
  fun updateProfilePicture_Offline() {
    profileViewModel.updateProfilePicture(profileViewModel, Uri.EMPTY)
    shadowOf(Looper.getMainLooper()).idle()
    assertEquals(profileViewModel._imageUrl.value, null)
  }

  @Test
  fun deleteCurrentUserProfile_Offline() {
    profileViewModel.currentUserId = "1"
    profileViewModel.deleteCurrentUserProfile(onSuccess = {}, onFailure = {})
    shadowOf(Looper.getMainLooper()).idle()
    verify(mockDocumentReference, Mockito.never()).set(any())
  }
}
