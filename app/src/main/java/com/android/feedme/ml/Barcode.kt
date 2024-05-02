package com.android.feedme.ml

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import com.android.feedme.api.HttpMethod
import com.android.feedme.api.httpRequestBarcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

/**
 * Scans a barcode from a given bitmap image using google ML kit. By default it's parametrized to
 * scan barcode of type EAN 13.
 *
 * @param bitmap The bitmap image containing the barcode to be scanned.
 * @param onSuccess Callback function to be executed when the barcode is successfully scanned. It
 *   takes a single parameter of type String, which represents the scanned barcode value. Default is
 *   an empty function.
 * @param onFailure Callback function to be executed when barcode scanning fails. It takes a single
 *   parameter of type Exception, which represents the exception occurred during the scanning
 *   process. Default is an empty function.
 */
fun barcodeScanner(
    bitmap: Bitmap?,
    onSuccess: (String) -> Unit = {},
    onFailure: (Exception) -> Unit = {}
) {
  val barcodeState = mutableStateOf<String?>(null)
  val specifications =
      BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_EAN_13).build()
  val image = bitmap?.let { InputImage.fromBitmap(it, 0) }
  val scanner = BarcodeScanning.getClient(specifications)
  if (image != null) {
    scanner.process(image).addOnSuccessListener { barcodeList ->
      if (barcodeList.isNotEmpty()) {
        barcodeState.value = barcodeList[(0)].rawValue
        barcodeState.value?.let { onSuccess(it) }
      } else {
        onFailure(Exception("Failed to detect a barcode in photo"))
      }
    }
  }
}

/**
 * Asynchronously extracts the product name from a barcode number by doing an http request.
 *
 * This function performs the extraction operation asynchronously, allowing the caller to provide
 * callbacks for both successful and failed outcomes.
 *
 * @param barcodeNB The barcode number for which the product name needs to be extracted.
 * @param onSuccess Callback function invoked when the product name extraction is successful. It
 *   takes a single parameter, [productName], representing the extracted product name.
 * @param onFailure Callback function invoked when an exception occurs during the extraction
 *   process. It takes a single parameter, [exception], representing the exception that occurred.
 * @throws Exception if an error occurs during the extraction process.
 */
suspend fun extractProductNameFromBarcode(
    barcodeNB: String,
    onSuccess: (String) -> Unit,
    onFailure: (Exception) -> Unit
) {
  try {
    onSuccess(httpRequestBarcode(HttpMethod.GET, barcodeNB, "fields=product_name"))
  } catch (e: Exception) {
    println(e.message)
    onFailure(e)
  }
}
