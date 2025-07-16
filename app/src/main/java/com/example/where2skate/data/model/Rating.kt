package com.example.where2skate.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Rating(
    val userId: String = "",
    val userName: String? = null, // Opcional, para mostrar quién puntuó
    val rating: Float = 0f, // ej. 1.0 a 5.0
    val comment: String? = null,
    @ServerTimestamp
    val ratedAt: Date? = null
)