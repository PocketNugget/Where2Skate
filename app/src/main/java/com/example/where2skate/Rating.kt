// File: model/Rating.kt
package com.example.where2skate.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Rating(
    var id: String = "", // ID del rating
    val skateparkId: String = "",
    val userId: String = "",
    val userName: String? = "Anónimo", // Nombre del usuario que puntúa
    val ratingValue: Float = 0.0f, // Puntuación (ej. 1.0 a 5.0)
    val comment: String? = null,
    @ServerTimestamp
    val createdAt: Date? = null
) {
    constructor() : this("", "", "", "Anónimo", 0.0f, null, null)
}