package com.android.feedme.ML

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

//input : bitmap image
//output : text in the image display in a popup ?
@Composable
fun TextRecognition(bitmap: Bitmap) : Text {
    val visionTextState = remember { mutableStateOf<Text?>(null) }
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    val image = InputImage.fromBitmap(bitmap, 0)
    val result = recognizer.process(image)
        .addOnSuccessListener { visionText ->
            // Task completed successfully
            visionTextState.value = visionText
        }
    return visionTextState.value ?: error("Text recognition result is null")
}

@Composable
fun TextProcessingAndDisplay(text : Text) {
    for (block in processedText.textBlocks) {
        val blockText = block.text
        val blockCornerPoints = block.cornerPoints
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
        }
    }

}
@Composable
fun PopUpTextDisplay(text : String){

}