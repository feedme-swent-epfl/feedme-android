package com.android.feedme.model.data

import java.time.Instant
import java.util.Date
import org.junit.Assert.*
import org.junit.Test

class CommentTest {

  @Test
  fun createAndRetrieveCommentProperties() {
    // Given
    val comment =
        Comment(
            userId = "user123",
            commentId = "user123",
            recipeId = "recipe456",
            photoURL = "https://example.com/photo.jpg",
            rating = 4.5,
            title = "Delicious!",
            content = "This recipe is fantastic!",
            creationDate = Date.from(Instant.now()))

    // Then
    assertEquals("user123", comment.commentId)
    assertEquals("recipe456", comment.recipeId)
    assertEquals("https://example.com/photo.jpg", comment.photoURL)
    assertEquals(4.5, comment.rating, 0.0)
    assertEquals("Delicious!", comment.title)
    // Additional assertions can be made for other fields
  }
}
