package com.android.feedme.test.camera

import android.Manifest
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import com.android.feedme.screen.GalleryScreen
import com.android.feedme.ui.camera.GalleryScreen
import com.android.feedme.ui.navigation.NavigationActions
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import java.io.File
import java.io.FileOutputStream
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GalleryTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()

  // Grant gallery permission for tests
  @get:Rule
  val permissionRule: GrantPermissionRule =
      if (Build.VERSION.SDK_INT <= 32)
          GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE)
      else GrantPermissionRule.grant(Manifest.permission.READ_MEDIA_IMAGES)

  @Test
  fun buttonsAndGalleryCorrectlyDisplayed() {
    goToGalleryScreen(10)

    ComposeScreen.onComposeScreen<GalleryScreen>(composeTestRule) {
      placeholderText { assertIsDisplayed() }

      addPhotoButton {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }
    }
  }

  @Test
  fun selectPhotoFromGallery() {
    goToGalleryScreen(10)

    val uri1 = Uri.parse("content://media/external/images/media/12345")
    val uri2 = Uri.parse("content://media/external/images/media/12346")
    val bitmapMock1 = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    val bitmapMock2 = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    saveBitmapToGallery(bitmapMock1, "myImage.png")

    // Prepare the mocks
    mockkStatic(ImageDecoder::class)
    every { ImageDecoder.createSource(any(), uri1) } returns mockk(relaxed = true)
    every { ImageDecoder.createSource(any(), uri2) } returns mockk(relaxed = true)
    every { ImageDecoder.decodeBitmap(any()) } returnsMany listOf(bitmapMock1, bitmapMock2)

    ComposeScreen.onComposeScreen<GalleryScreen>(composeTestRule) {
      addPhotoButton {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }
    }

    composeTestRule.waitForIdle()
    val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    device.click(60, 780)
    composeTestRule.waitForIdle()

    Log.d("GalleryTest", device.displayHeight.toString())
    Log.d("GalleryTest", device.displayWidth.toString())
    device.click(950, 2050)
  }

  private fun goToGalleryScreen(maxItems: Int) {
    val navActions = mockk<NavigationActions>()
    every { navActions.canGoBack() } returns true
    composeTestRule.setContent { GalleryScreen(navActions, maxItems) }
    composeTestRule.waitForIdle()
  }
}

private fun saveBitmapToGallery(bitmap: Bitmap, fileName: String) {
  // Check for external storage availability
  if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
    return
  }

  // Get the Pictures directory
  val picturesDirectory =
      Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
  val imageFile = File(picturesDirectory, fileName)

  try {
    val file = File(fileName)
    val fOut = FileOutputStream(file)
    fOut.flush()
    fOut.close()
  } catch (e: Exception) {
    e.printStackTrace()
    Log.i(null, "Save file error!")
  }
}
