package com.android.feedme.test

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.CurrentScreen
import com.android.feedme.MainActivity
import com.android.feedme.screen.ProfileScreen
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileTest {
    @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()
    @get:Rule val intentsTestRule = IntentsTestRule(MainActivity::class.java)

    @Before
    fun setup() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            activity.setScreen(CurrentScreen.PROFILE) // For ProfileTest
        }
    }

    @Test
    fun profileBoxAndComponentsCorrectlyDisplayed() {
        ComposeScreen.onComposeScreen<ProfileScreen>(composeTestRule) {
            profileBox { assertIsDisplayed() }

            profileName { assertIsDisplayed() }

            profileIcon { assertIsDisplayed() }

            profileBio { assertIsDisplayed() }

            followerButton {
                assertIsDisplayed()
                assertHasClickAction()
            }

            followingButton {
                assertIsDisplayed()
                assertHasClickAction()
            }

            editButton {
                assertIsDisplayed()
                assertHasClickAction()
            }

            shareButton {
                assertIsDisplayed()
                assertHasClickAction()
            }
        }
    }
}