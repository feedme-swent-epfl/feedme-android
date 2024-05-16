package com.android.feedme.ml

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.android.feedme.api.HttpMethod
import com.android.feedme.api.httpRequestBarcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import org.json.JSONException
import org.json.JSONObject

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
fun barcodeScan(
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
 * @param onSuccess Callback function invoked when the product name extraction is successful.
 * @param onFailure Callback function invoked when an exception occurs during the extraction
 *   process.
 * @throws Exception if an error occurs during the extraction process.
 */
suspend fun extractProductInfoFromBarcode(
    barcodeNB: String,
    onSuccess: (ProductInfo?) -> Unit = {},
    onFailure: (Exception) -> Unit = {}
) {
  try {
      val result = httpRequestBarcode(HttpMethod.GET, barcodeNB, "fields=product_name")
      println(result)
    onSuccess(parseJsonString(httpRequestBarcode(HttpMethod.GET, barcodeNB, "fields=product_name")))
  } catch (e: Exception) {
    e.message?.let { Log.i(e.message, it) }
    onFailure(e)
  }
}

/**
 * Parses a JSON string to extract product information into a ProductInfo object.
 *
 * @param jsonString The JSON string containing product details (obtained after httpRequest).
 * @param onFailure  Callback invoked on parsing failure, default is an empty lambda.
 *
 * @return A ProductInfo object if parsing is successful; null otherwise.
 */
fun parseJsonString(jsonString: String, onFailure: (Exception) -> Unit = {}): ProductInfo? {
    try {
        val jsonObject = JSONObject(jsonString)
        return ProductInfo(
            code = jsonObject.getString("code"),
            productName = jsonObject.getJSONObject("product").getString("product_name"),
            status = jsonObject.getInt("status")
        )
    } catch (e: JSONException) {
        e.message?.let { Log.e("Error parsing Json string for barcode : ", it) }
        onFailure(Exception("Failed to parse Json information of barcode."))
        return null
    }
}

/**
 * Data class representing product information.
 *
 * @property code The product barcode code.
 * @property productName The name of the product.
 * @property status The status of the product (1 if successful, 0 if no information).
 */
data class ProductInfo(
    val code: String,
    val productName: String,
    val status: Int
)
