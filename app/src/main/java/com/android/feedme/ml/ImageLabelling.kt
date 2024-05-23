package com.android.feedme.ml

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions

val localModel = LocalModel.Builder().setAssetFilePath("Model1.tflite").build()

fun labelExtraction(bitmap : Bitmap,
                     onSuccess: (String) -> Unit = {},
                     onFailure: (Exception) -> Unit = {}){

    val image = InputImage.fromBitmap(bitmap, 0)
    val customObjectDetectorOptions =
        CustomObjectDetectorOptions.Builder(localModel)
            .setDetectorMode(CustomObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .setClassificationConfidenceThreshold(0.5f)
            .setMaxPerObjectLabelCount(3)
            .build()
    val objectDetector =
        ObjectDetection.getClient(customObjectDetectorOptions)

    objectDetector
        .process(image)
        .addOnSuccessListener { results ->
            onSuccess(labelProcessing(results))
        }
        .addOnFailureListener{e ->
            e.message?.let { Log.e("Image Labeling", it) }
            onFailure(e)
        }
}

fun labelProcessing(listObject : List<DetectedObject>): String {
    var text = ""
    for (detectedObject in listObject) {
        val boundingBox = detectedObject.boundingBox
        for (label in detectedObject.labels) {
            text = label.text
            println(text)
            val index = label.index
            val confidence = label.confidence
            println(confidence)
        }
    }
    return text
}