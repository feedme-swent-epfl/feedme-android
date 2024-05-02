package com.android.feedme.ml

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
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
