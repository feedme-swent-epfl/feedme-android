package com.android.feedme.test.ui

import androidx.compose.ui.geometry.Rect
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.ml.bestLabel
import com.android.feedme.ml.labelProcessing
import com.google.mlkit.vision.objects.DetectedObject
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageLabellingTest : TestCase() {

  // bestLabel returns the label with highest confidence score among food labels
  @Test
  fun bestLabelCase1() {
    val labelList = mapOf("apple" to 0.9f, "banana" to 0.8f, "car" to 0.7f, "dog" to 0.6f)
    println(bestLabel(labelList))
    val bestLabel = bestLabel(labelList)

    assertEquals("apple", bestLabel)
  }

  // bestLabel returns the first label when multiple labels have the same highest confidence score
  @Test
  fun bestLabelCase2() {
    val labelList = mapOf("apple" to 0.9f, "banana" to 0.9f, "car" to 0.7f, "dog" to 0.6f)

    val bestLabel = bestLabel(labelList)

    assertEquals("apple", bestLabel)
  }

  // bestLabel returns an empty string when there are no food labels
  @Test
  fun bestLabelCase3() {
    val labelList = mapOf("car" to 0.7f, "dog" to 0.6f)

    val bestLabel = bestLabel(labelList)

    assertEquals("", bestLabel)
  }
  // labelProcessing test with empty list
  @Test
  fun labelProcessingCase1() {
    val emptyList = emptyList<DetectedObject>()
    val result = labelProcessing(emptyList)
    assertTrue(result.isEmpty())
  }
  // labelProcessing test with single object and single label
  @Test
  fun labelProcessingCase2() {
    val detectedObject =
        DetectedObject(
            android.graphics.Rect(10, 20, 100, 150),
            123,
            listOf(DetectedObject.Label("car", 0.9f, 0)))
    val result = labelProcessing(listOf(detectedObject))
    assertEquals(1, result.size)
    assertTrue(result.containsKey("car"))
    assertEquals(0.9f, result["car"])
  }
  // labelProcessing test with multiple objects and labels
  @Test
  fun labelProcessingCase3() {
    val detectedObject1 =
        DetectedObject(
            android.graphics.Rect(10, 20, 100, 150),
            123,
            listOf(DetectedObject.Label("car", 0.8f, 0), DetectedObject.Label("truck", 0.7f, 1)))

    val detectedObject2 =
        DetectedObject(
            android.graphics.Rect(10, 20, 100, 150),
            123,
            listOf(DetectedObject.Label("person", 0.9f, 0), DetectedObject.Label("dog", 0.6f, 1)))
    val result = labelProcessing(listOf(detectedObject1, detectedObject2))
    assertEquals(4, result.size)
    assertTrue(result.containsKey("car"))
    assertEquals(0.8f, result["car"])
    assertTrue(result.containsKey("truck"))
    assertEquals(0.7f, result["truck"])
    assertTrue(result.containsKey("person"))
    assertEquals(0.9f, result["person"])
    assertTrue(result.containsKey("dog"))
    assertEquals(0.6f, result["dog"])
  }
  // labelProcessing test with duplicate labels
  @Test
  fun labelProcessingCase4() {
    val detectedObject1 =
        DetectedObject(
            android.graphics.Rect(10, 20, 100, 150),
            123,
            listOf(DetectedObject.Label("car", 0.8f, 0), DetectedObject.Label("car", 0.7f, 1)))

    val detectedObject2 =
        DetectedObject(
            android.graphics.Rect(10, 20, 100, 150),
            123,
            listOf(DetectedObject.Label("car", 0.9f, 0)))
    val result = labelProcessing(listOf(detectedObject1, detectedObject2))
    assertEquals(1, result.size)
    assertTrue(result.containsKey("car"))
    assertEquals(0.9f, result["car"])
  }
}
