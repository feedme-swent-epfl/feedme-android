package com.android.feedme.ml

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions

// Our ML model. Will find in the future a better one.
private val LOCAL_MODEL = LocalModel.Builder().setAssetFilePath("Model1.tflite").build()
// List of food labels among all the labels of our model
private val FOOD_LABELS: List<String> =
    listOf(
        "apple",
        "croissant",
        "cucumber",
        "radish",
        "hot dog",
        "burrito",
        "popcorn",
        "cheese",
        "muffin",
        "snack",
        "juice",
        "cookie",
        "dessert",
        "drink",
        "zucchini",
        "guacamole",
        "food",
        "fruit",
        "french fries",
        "Egg",
        "grape",
        "pineapple",
        "cake",
        "salad",
        "candy",
        "broccoli",
        "bell pepper",
        "turkey",
        "pomegranate",
        "doughnut",
        "watermelon",
        "cantaloupe",
        "sandwich",
        "shrimp",
        "crab",
        "hamburger")

/**
 * Extracts objects from the given bitmap using a custom object detector from google ML kit.
 *
 * This function processes the provided bitmap to detect objects. It invokes the onSuccess callback
 * with the list of detected objects if detection is successful, or the onFailure callback with an
 * exception if detection fails.
 *
 * @param bitmap The bitmap from which to extract objects.
 * @param onSuccess A callback to be invoked with the detected objects if extraction is successful.
 * @param onFailure A callback to be invoked with an exception if extraction fails.
 */
fun objectExtraction(
    bitmap: Bitmap,
    onSuccess: (List<DetectedObject>) -> Unit = {},
    onFailure: (Exception) -> Unit = {}
) {

  val image = InputImage.fromBitmap(bitmap, 0)
  val customObjectDetectorOptions =
      CustomObjectDetectorOptions.Builder(LOCAL_MODEL)
          .setDetectorMode(CustomObjectDetectorOptions.SINGLE_IMAGE_MODE)
          .enableMultipleObjects()
          .enableClassification()
          .setClassificationConfidenceThreshold(0.35f) // 0.5f
          .setMaxPerObjectLabelCount(5) // 3
          .build()
  val objectDetector = ObjectDetection.getClient(customObjectDetectorOptions)
  objectDetector
      .process(image)
      .addOnSuccessListener { results -> onSuccess(results) }
      .addOnFailureListener { e ->
        e.message?.let { Log.e("Image Labeling", it) }
        onFailure(e)
      }
}

/**
 * This function iterates through the list of detected objects, extracts the labels from each
 * object, and fills a map with the label text as the key and the confidence score as the value.
 *
 * @param listObject The list of detected objects to process.
 * @return A mutable map where the keys are label texts and the values are their corresponding
 *   confidence scores.
 */
fun labelProcessing(listObject: List<DetectedObject>): MutableMap<String, Float> {
  val labelList: MutableMap<String, Float> = emptyMap<String, Float>().toMutableMap()
  for (detectedObject in listObject) {
    for (label in detectedObject.labels) {
      labelList[label.text] = label.confidence
    }
  }
  return labelList
}

/**
 * Determines the best label from a map of labels with confidence scores.
 *
 * This function filters the input label map to only include labels that are considered food labels.
 * It then identifies the label with the highest confidence score and returns it.
 *
 * @param labelList A map of labels with their corresponding confidence scores.
 * @return The label with the highest confidence score among the food labels.
 */
fun bestLabel(labelList: Map<String, Float>): String {
  Log.d("LabelList", labelList.toString())
  val foodLabels =
      labelList.filterKeys { label ->
        FOOD_LABELS.map { it -> it.lowercase() }.contains(label.lowercase())
      }
  Log.d("FoodLabels", foodLabels.toString())
  val maxConfidence = foodLabels.values.maxOrNull()
  if (foodLabels.isEmpty() || maxConfidence == null) {
    return ""
  }
  return foodLabels.filterValues { it == maxConfidence }.keys.first()
}
