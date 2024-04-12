package com.android.feedme.resources.mock

object MockServiceLocator {
  private val services = mutableMapOf<String, Any>()

  fun <T : Any> registerService(key: String, service: T) {
    services[key] = service
  }

  @Suppress("UNCHECKED_CAST")
  fun <T> getService(key: String): T {
    return services[key] as T
  }
}
