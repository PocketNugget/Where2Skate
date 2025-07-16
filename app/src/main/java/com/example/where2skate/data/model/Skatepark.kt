package com.example.where2skate.data.model

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Skatepark(
    val id: String = "", // ID del documento en Firestore
    val name: String = "",
    val description: String? = null,
    val location: GeoPoint? = null, // Latitud y Longitud
    val address: String? = null, // Dirección textual (opcional)
    val creatorId: String = "",
    val creatorName: String? = null, // Para mostrar quién lo añadió
    @ServerTimestamp
    val createdAt: Date? = null,
    val averageRating: Double = 0.0,
    val ratingCount: Int = 0
    // Podrías tener una subcolección para ratings detallados
)