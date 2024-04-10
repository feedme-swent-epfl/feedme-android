package com.android.feedme.model.data

import androidx.test.core.app.ApplicationProvider
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

@RunWith(RobolectricTestRunner::class)
class AuthViewModelTest {

    @Mock
    private lateinit var profileRepository: ProfileRepository

    private lateinit var authViewModel: AuthViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
            FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
        }
        authViewModel = AuthViewModel()
    }

    @Test
    fun `linkOrCreateProfile creates new profile if none exists`() {
        val googleId = "googleId123"
        val name = "Test User"
        val email = "test@example.com"
        val photoUrl = "http://example.com/photo.jpg"

        // Mock ProfileRepository to simulate that no profile exists for the given googleId
        `when`(profileRepository.getProfile(eq(googleId), any(), any())).thenAnswer {
            val onFailure = it.getArgument<(Exception) -> Unit>(2)
            onFailure(Exception("Profile not found"))
        }

        // Call linkOrCreateProfile with mocked parameters
        authViewModel.linkOrCreateProfile(googleId, name, email, photoUrl, onSuccess = {
            // This block should execute, indicating success
        }, onFailure = {
            fail("Expected profile creation to succeed but it failed")
        })

        // Verify that addProfile was called since no profile exists
        verify(profileRepository).addProfile(any(), any(), any())
    }

    @Test
    fun `linkOrCreateProfile does nothing if profile already exists`() {
        val existingGoogleId = "googleId123"

        // Mock ProfileRepository to simulate an existing profile
        `when`(profileRepository.getProfile(existingGoogleId, {  }, { })).thenAnswer {
            val onSuccess = it.getArgument<(Profile?) -> Unit>(1)
            onSuccess(Profile(existingGoogleId, "Existing User", "", "existing@example.com", "", "", emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
        }

        // Call linkOrCreateProfile with parameters that would match an existing profile
        authViewModel.linkOrCreateProfile(existingGoogleId, "Existing User", "existing@example.com", "", onSuccess = {
            // Success block should execute
        }, onFailure = {
            fail("Expected to not create a new profile but it failed")
        })

        // Verify that addProfile was never called because a matching profile exists
        verify(profileRepository, never()).addProfile(any(), any(), any())
    }
}

