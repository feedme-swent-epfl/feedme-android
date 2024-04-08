package com.android.feedme.model.data

import android.icu.text.SimpleDateFormat

data class Comment(
    val authorId : String,
    val recipeId : String,
    val photoURL : String,
    val rating : Double,
    val time : Double,
    val title : String,
    val content : String,
    val creationDate : SimpleDateFormat
)
