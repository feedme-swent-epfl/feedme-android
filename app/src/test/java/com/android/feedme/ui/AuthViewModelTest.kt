package com.android.feedme.ui

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.viewmodel.AuthViewModel
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class AuthViewModelTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot
  @Mock private lateinit var mockFirebaseAuth: FirebaseAuth
  @Mock private lateinit var mockFirebaseUser: FirebaseUser

  @Mock private lateinit var navigationActions: NavigationActions

  private lateinit var profileRepository: ProfileRepository

  private lateinit var authViewModel: AuthViewModel

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
    `when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)

    authViewModel = AuthViewModel()
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

    var bool = false
    authViewModel.linkOrCreateProfile(
        profileId, "johndoe", "john@example.com", "ss", { bool = true }, {}, {})

    shadowOf(Looper.getMainLooper()).idle()
    authViewModel.linkOrCreateProfile(
        "aaa", "johndoe", "john@example.com", "ss", { bool = true }, {}, {})

    shadowOf(Looper.getMainLooper()).idle()

    authViewModel.linkOrCreateProfile(
        profileId, "dd", "john@example.com", "ss", { bool = true }, {}, {})

    authViewModel.authenticateWithGoogle("johndoe", {}, {}, {})

    shadowOf(Looper.getMainLooper()).idle()
    assertTrue(bool)
  }

  @Test
  fun fetchUserProfileOffline_UserSignedIn_Success() {
    `when`(mockFirebaseUser.uid).thenReturn("user123")
    `when`(mockDocumentReference.get(Source.CACHE))
        .thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)
    val expectedProfile =
        Profile(
            "user123",
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
    `when`(mockDocumentSnapshot.toObject(Profile::class.java)).thenReturn(expectedProfile)

    authViewModel.fetchUserProfileOffline(navigationActions)

    shadowOf(Looper.getMainLooper()).idle()

    // verify(navigationActions).navigateTo(TOP_LEVEL_DESTINATIONS[1])
  }

  @Test
  fun fetchUserProfileOffline_UserSignedIn_ProfileNotExists() {
    `when`(mockFirebaseUser.uid).thenReturn("user123")
    `when`(mockDocumentReference.get(Source.CACHE))
        .thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(false)

    authViewModel.fetchUserProfileOffline(navigationActions)

    shadowOf(Looper.getMainLooper()).idle()

    verify(navigationActions, never()).navigateTo(TOP_LEVEL_DESTINATIONS[0])
    verify(navigationActions, never()).navigateTo(TOP_LEVEL_DESTINATIONS[1])
  }

  @Test
  fun fetchUserProfileOffline_UserSignedIn_Failure() {
    `when`(mockFirebaseUser.uid).thenReturn("user123")
    `when`(mockDocumentReference.get(Source.CACHE))
        .thenReturn(Tasks.forException(Exception("Cache fetch failed")))

    authViewModel.fetchUserProfileOffline(navigationActions)

    shadowOf(Looper.getMainLooper()).idle()

    verify(navigationActions, never()).navigateTo(TOP_LEVEL_DESTINATIONS[0])
    verify(navigationActions, never()).navigateTo(TOP_LEVEL_DESTINATIONS[1])
  }

  @Test
  fun fetchUserProfileOffline_NoUserSignedIn() {
    `when`(mockFirebaseAuth.currentUser).thenReturn(null)

    authViewModel.fetchUserProfileOffline(navigationActions)

    shadowOf(Looper.getMainLooper()).idle()

    verify(navigationActions, never()).navigateTo(TOP_LEVEL_DESTINATIONS[0])
    verify(navigationActions, never()).navigateTo(TOP_LEVEL_DESTINATIONS[1])
  }
}
