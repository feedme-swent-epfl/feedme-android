package com.android.feedme.model.data

import java.util.Date

data class Comment(
    var commentId: String = "DEFAULT_ID",
    val userId: String,
    val recipeId: String,
    var photoURL: String,
    val rating: Double,
    val title: String,
    val content: String,
    val creationDate: Date
)
