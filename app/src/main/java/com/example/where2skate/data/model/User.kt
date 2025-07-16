package com.example.where2skate.data.model

data class User(
    val uid: String = "",
    val email: String? = null,
    val displayName: String? = null
    // Puedes añadir más campos como foto de perfil, etc.
)