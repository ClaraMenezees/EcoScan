package com.ifpe.ecoscan.model

data class Product(
    val id: String,
    val barcode: String?,
    val name: String?,
    val genericName: String? = null,
    val brand: String? = null,
    val imageUrl: String? = null,
    val ingredientsText: String? = null,
    val nutritionGrade: String? = null,
    val ecoscoreGrade: String? = null,
    val nutriments: Nutriments? = null,
    // campos opcionais para UI
    val packagingTags: List<String>? = null,
    val packagingTextEn: String? = null
)
