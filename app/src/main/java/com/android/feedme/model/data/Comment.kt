package com.android.feedme.model.data

import java.util.Date

data class Comment(
    val commentId: String,
    val userId: String,
    val recipeId: String,
    val photoURL: String,
    val rating: Double,
    val title: String,
    val content: String,
    val creationDate: Date
)
