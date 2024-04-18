package com.android.feedme.ui.auth

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import io.mockk.every
import io.mockk.mockk

fun mockGoogleSignInAccount(): GoogleSignInAccount {
    val account = mockk<GoogleSignInAccount>(relaxed = true)
    // Mock necessary methods of GoogleSignInAccount as needed for the test
    every { account.id } returns "123456789"
    every { account.displayName } returns "Test User"
    every { account.email } returns "test@example.com"
    return account
}

object AppConfig {
    var isTestMode = true
}