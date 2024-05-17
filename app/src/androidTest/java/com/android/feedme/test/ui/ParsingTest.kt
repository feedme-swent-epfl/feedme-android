package com.android.feedme.test.ui

import com.android.feedme.ml.ProductInfo
import com.android.feedme.ml.parseJsonString
import junit.framework.TestCase.assertEquals
import org.junit.Test

class ParsingTest {

  @Test
  fun parsingJsonStringValid() {
    val validJsonString =
        """
            {
                "code": "123456",
                "product": {
                    "product_name": "Test Product"
                },
                "status": 1
            }
        """
            .trimIndent()

    val expectedProductInfo = ProductInfo(code = "123456", productName = "Test Product", status = 1)

    val actualProductInfo = parseJsonString(validJsonString)

    assertEquals(expectedProductInfo, actualProductInfo)
  }

  @Test
  fun parsingJsonStringInvalid() {
    val invalidJsonString =
        """
            {
                "code": "123456",
                "product": {
                    "product_name": "Test Product"
                }
            }
        """
            .trimIndent()

    var exceptionThrown = false
    val onFailure: (Exception) -> Unit = { exceptionThrown = true }

    val actualProductInfo = parseJsonString(invalidJsonString, onFailure)

    assertEquals(null, actualProductInfo)
    assertEquals(true, exceptionThrown)
  }
}
