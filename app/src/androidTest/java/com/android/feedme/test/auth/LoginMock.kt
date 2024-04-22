package com.android.feedme.test.auth

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import io.mockk.every
import io.mockk.mockk

fun mockGoogleSignInAccount(): GoogleSignInAccount {
  val account = mockk<GoogleSignInAccount>(relaxed = true)
  // Mock necessary methods of GoogleSignInAccount as needed for the test
  every { account.id } returns "ID_DEFAULT"
  every { account.displayName } returns "NAME_DEFAULT"
  every { account.email } returns "EMAIL_DEFAULT"

  return account
}
