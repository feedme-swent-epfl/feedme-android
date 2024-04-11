package com.android.feedme.model

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.ui.profile.ProfileViewModel
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
class ProfileViewModelTest {

    @Mock private lateinit var mockFirestore: FirebaseFirestore

    @Mock private lateinit var mockDocumentReference: DocumentReference

    @Mock private lateinit var mockCollectionReference: CollectionReference

    @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot

    private lateinit var profileRepository: ProfileRepository

    private lateinit var profileViewModel: ProfileViewModel

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

        profileViewModel = ProfileViewModel()

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

        assertTrue(profileViewModel.profile.value!!.email == expectedProfile.email)



    }

}
