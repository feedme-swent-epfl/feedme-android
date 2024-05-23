package com.android.feedme.ml

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.text.Text

fun labelExtraction(bitmap : Bitmap,
                     onSuccess: (String) -> Unit = {},
                     onFailure: (Exception) -> Unit = {}){

    val image = InputImage.fromBitmap(bitmap, 0)
    val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
    labeler.process(image)
        .addOnSuccessListener { labels ->
            val processedLabel = labelProcessing(labels)
            onSuccess(processedLabel)
        }
        .addOnFailureListener { e ->
            e.message?.let { Log.e("Image Labeling", it) }
            onFailure(e)
        }
}

fun labelProcessing(labelList : List<ImageLabel>): String {
    var text = ""
    var confidence = 0.0f
    var index = 0
    for (label in labelList) {
        text = label.text
        println(text)
        confidence = label.confidence
        index = label.index
    }
    return text
}