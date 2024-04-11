package com.android.feedme.model.data

import java.util.Date

data class Comment(
    val authorId: String,
    val recipeId: String,
    val photoURL: String,
    val rating: Double,
    val time: Double,
    val title: String,
    val content: String,
    val creationDate: Date
)
