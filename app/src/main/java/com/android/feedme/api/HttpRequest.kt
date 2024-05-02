package com.android.feedme.api

import java.net.HttpURLConnection
import java.net.URL


fun httpRequestBarcode(requestMethod: HttpMethod, urlEndPoint: String, barcodeNb: String): String {
    //val url = "https://world.openfoodfacts.net/api/v2/product/7640150491001?fields=product_name"
    val url = "$urlEndPoint$barcodeNb?fields=product_name"
    var response = ""
    try {
        val urlObj = URL(url)
        val connection = urlObj.openConnection() as HttpURLConnection
        connection.requestMethod = requestMethod.stringValue()

        response = connection.inputStream.bufferedReader().use { it.readText()}

    } catch (e: Exception) {
        println("HTTP request failed with url : $url")
        println("Error: ${e.message}")
    }
    return response
}

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
