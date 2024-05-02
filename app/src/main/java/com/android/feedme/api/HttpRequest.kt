package com.android.feedme.api

import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val OPEN_FOOD_FACTS_URL_END_POINT = "https://world.openfoodfacts.net/api/v2/product/"

/**
 * Makes an HTTP request to retrieve product name (only) information based on a barcode number. The
 * request is made to world.openfoodfacts.net.
 * This function performs the HTTP request asynchronously using coroutines and the IO dispatcher.
 *
 * @param requestMethod The HTTP method to use for the request (e.g., HttpMethod.GET).
 * @param barcodeNb The barcode number used to retrieve product information.
 * @param urlFields Additional fields to include in the URL query string.
 * @return A string containing the response from the HTTP request.
 */
suspend fun httpRequestBarcode(
    requestMethod: HttpMethod,
    barcodeNb: String,
    urlFields: String
): String {
  return withContext(Dispatchers.IO) {
    val url = "$OPEN_FOOD_FACTS_URL_END_POINT$barcodeNb?$urlFields"
    var response = ""
    try {
      val urlObj = URL(url)
      val connection = urlObj.openConnection() as HttpURLConnection
      connection.requestMethod = requestMethod.stringValue()

      response = connection.inputStream.bufferedReader().use { it.readText() }
    } catch (e: Exception) {
      println("HTTP request failed with url : $url")
      if (e.message != null) {
        println("Error http: ${e.message}")
      } else {
        println("Error http: ${e.javaClass.simpleName}")
      }
      e.printStackTrace()
    }
    response
  }
}

/** Represents possible HTTP methods for making requests. */
enum class HttpMethod {
  GET,
  POST,
  PUT,
  DELETE,
  PATCH;

  fun stringValue(): String {
    return when (this) {
      GET -> "GET"
      POST -> "POST"
      PUT -> "PUT"
      DELETE -> "DELETE"
      PATCH -> "PATCH"
    }
  }
}
