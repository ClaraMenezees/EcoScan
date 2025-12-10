package com.ifpe.ecoscan.model

data class Nutriments(
    val sugars100g: Double? = null,
    val fat100g: Double? = null,
    val saturatedFat100g: Double? = null,
    val sodium100g: Double? = null, // pode vir em mg; ClassificationUtils converte heurística
    val salt100g: Double? = null,
    val fiber100g: Double? = null,
    val proteins100g: Double? = null
)
