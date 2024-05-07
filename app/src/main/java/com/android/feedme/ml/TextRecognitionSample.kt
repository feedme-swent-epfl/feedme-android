package com.android.feedme.ml

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

/**
 * Recognizes text in the given bitmap image asynchronously, using the Google ML Kit.
 *
 * @param bitmap The bitmap containing the image with text to be recognized.
 * @param onSuccess A lambda function to be called when text recognition succeeds. It receives a
 *   Text object as a parameter.
 * @param onFailure A lambda function to be called when text recognition fails. It receives an
 *   Exception as a parameter.
 */

//,
//    onFailure: (Exception) -> Unit = {}
fun TextRecognition(
    bitmap: Bitmap?,
    onSuccess: (Text) -> Unit = {}
) {
  val visionTextState = mutableStateOf<Text?>(null)
  val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
  val image = bitmap?.let { InputImage.fromBitmap(it, 0) }
  if (image != null) {
    recognizer
        .process(image)
        .addOnSuccessListener { visionText ->
          // Task completed successfully
          visionTextState.value = visionText
          onSuccess(visionText)
        }
  }
}

/**
 * Processes the provided [Text] object and returns a concatenated string containing the text of
 * each block.
 *
 * This function iterates through each block, line, and element in the provided [Text] object,
 * extracting their text content and bounding box information. It then returns a string containing
 * the text of each block concatenated together.
 *
 * @param text The [Text] object containing the text to be processed.
 * @return A concatenated string containing the text of each block.
 *
 * TODO("A lot of work on exact image processing an display of the relevant information's in a smart
 *   way")
 */

fun TextProcessing(text: Text): String {
  var blockText = ""
  for (block in text.textBlocks) {
    blockText += block.text
    /*val blockCornerPoints = block.cornerPoints
    val blockFrame = block.boundingBox
    for (line in block.lines) {
      val lineText = line.text
      val lineCornerPoints = line.cornerPoints
      val lineFrame = line.boundingBox
      for (element in line.elements) {
        val elementText = element.text
        val elementCornerPoints = element.cornerPoints
        val elementFrame = element.boundingBox
      }
    }*/
  }
  return blockText
}
/**
 * Displays an overlay text field when [isVisible] is true. When you click outside of the text field
 * the [onDismiss] function is called.
 *
 * @param isVisible Whether the overlay text field should be displayed.
 * @param onDismiss Callback function to be invoked when the overlay text field is dismissed.
 * @param text The text to display within the overlay text field.
 */
@Composable
fun OverlayTextField(isVisible: Boolean, onDismiss: () -> Unit, text: String = "") {
  if (isVisible) {
    Dialog(
        onDismissRequest = { onDismiss() },
        content = {
          Surface(
              shape = RectangleShape,
              color = Color.White,
              modifier = Modifier.padding(16.dp).testTag("ML Text Box")) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                  Text(text = text, modifier = Modifier.testTag("ML Text Box Inside"))
                }
              }
        })
  }
}
