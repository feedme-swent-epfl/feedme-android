package com.android.feedme.model.data

import org.junit.Assert.*
import org.junit.Test

class ProfileTest {

  @Test
  fun createAndRetrieveProfileProperties() {
    // Given
    val followers = listOf("user1", "user2")
    val following = listOf("user3", "user4")
    val filters = listOf("filter1", "filter2")
    val recipeList = listOf<Recipe>() // Assuming recipes are created elsewhere or mocked
    val commentList = listOf("comment1", "comment2")

    val profile =
        Profile(
            id = "1",
            name = "John Doe",
            username = "johndoe123",
            email = "john@example.com",
            description = "Food enthusiast",
            imageUrl = "https://example.com/johndoe.jpg",
            followers = followers,
            following = following,
            filter = filters,
            recipeList = recipeList,
            commentList = commentList)

    // Then
    assertEquals("John Doe", profile.name)
    assertEquals("johndoe123", profile.username)
    assertEquals("john@example.com", profile.email)
    assertEquals("Food enthusiast", profile.description)
    assertEquals(followers, profile.followers)
    assertEquals(following, profile.following)
    assertEquals(filters, profile.filter)
    // Additional assertions for `recipeList` and `commentList` as needed
  }
}
