package com.android.feedme.test.api

import com.android.feedme.api.HttpMethod
import com.android.feedme.api.httpRequestBarcode
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler
import java.net.URLStreamHandlerFactory
import org.junit.Assert.assertEquals
import org.junit.Test

class HttpRequestBarcodeTest {

  @Test
  suspend fun testHttpRequestBarcode() {
    val barcodeNb = "7640150491001"
    val urlFields = "fields=product_name"

    // Mocking the HTTP request by providing a custom URL for testing
    val testEndPoint = "https://world.openfoodfacts.net/api/v2/product/"
    val expectedUrl = "$testEndPoint$barcodeNb?$urlFields"

    // Mocking the response for the given test
    val expectedResponse =
        "{\"code\":\"7640150491001\",\"product\":{\"product_name\":\"El Tony Mate\"}," +
            "\"status\":1,\"status_verbose\":\"product found\"}"

    // Mocking the HTTP method for the test
    val requestMethod = HttpMethod.GET

    // Mocking the HTTP response
    val mockedHttpResponse = MockHttpResponse(expectedResponse)

    // Mocking the URL connection for testing
    val mockedUrlConnection = MockHttpURLConnection(mockedHttpResponse)

    // Mocking the URL object for testing
    val mockedUrl = URL(expectedUrl)

    // Setting up the URL stream handler factory mock
    UrlStreamHandlerFactoryMocker.setUrlStreamHandlerFactoryMock(
        MockUrlStreamHandlerFactory(mockedUrlConnection))

    // Executing the function to be tested
    val actualResponse = httpRequestBarcode(requestMethod, barcodeNb, urlFields)

    // Asserting that the response matches the expected response
    assertEquals(expectedResponse, actualResponse)
  }
}

// Mock classes for URL, URLConnection, and HttpResponse
class MockHttpURLConnection(private val response: MockHttpResponse) : HttpURLConnection(null) {
  override fun connect() {
    // No implementation needed
  }

  override fun getInputStream(): InputStream {
    return ByteArrayInputStream(response.response.toByteArray())
  }

  override fun disconnect() {
    // No implementation needed
  }

  override fun usingProxy(): Boolean {
    return false
  }
}

class MockHttpResponse(val response: String)

// Mock URL stream handler factory
class MockUrlStreamHandlerFactory(private val mockedUrlConnection: HttpURLConnection) :
    URLStreamHandlerFactory {
  override fun createURLStreamHandler(protocol: String?): URLStreamHandler {
    return MockUrlStreamHandler(mockedUrlConnection)
  }
}

class MockUrlStreamHandler(private val mockedUrlConnection: HttpURLConnection) :
    URLStreamHandler() {
  override fun openConnection(url: URL): URLConnection {
    return mockedUrlConnection
  }
}

// Mock URL stream handler factory mocker
object UrlStreamHandlerFactoryMocker {
  private lateinit var urlStreamHandlerFactoryMock: URLStreamHandlerFactory

  fun setUrlStreamHandlerFactoryMock(mock: URLStreamHandlerFactory) {
    urlStreamHandlerFactoryMock = mock
    URL.setURLStreamHandlerFactory(urlStreamHandlerFactoryMock)
  }

  fun getUrlStreamHandlerFactoryMock(): URLStreamHandlerFactory {
    return urlStreamHandlerFactoryMock
  }
}
