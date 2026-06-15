package com.king.veganapp.model

data class Restaurant(
    val id: String = "",
    val image: Int= 0,
    val name: String = "",
    val rating: String = "",
    val distance: String = "",
    val isVegan: Boolean = false, // 🌿 Vegan badge
    val isOpen: Boolean = true,   // 🟢 Open / Closed

    var isFavorite: Boolean = false, // ❤️ Local + Firebase user data

    val lat: Double = 0.0,
    val lng: Double = 0.0
)
