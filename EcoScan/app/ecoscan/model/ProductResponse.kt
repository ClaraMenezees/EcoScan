package com.ifpe.ecoscan.model

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @SerializedName("status") val status: Int = 0,
    @SerializedName("product") val product: ProductApi? = null
)

data class ProductApi(
    @SerializedName("id") val id: String? = null,
    @SerializedName("product_name") val product_name: String? = null,
    @SerializedName("generic_name") val generic_name: String? = null,
    @SerializedName("brands") val brands: String? = null,
    @SerializedName("ecoscore_grade") val ecoscore_grade: String? = null,
    @SerializedName("nutrition_grade_fr") val nutrition_grade: String? = null,
    @SerializedName("image_front_url") val image_front_url: String? = null,
    @SerializedName("ingredients_text") val ingredients_text: String? = null,
    // nutriments será mapeado para a nossa classe Nutriments abaixo
    @SerializedName("nutriments") val nutriments: NutrimentsApi? = null,
    @SerializedName("packaging_tags") val packaging_tags: List<String>? = null,
    @SerializedName("packaging_text_en") val packaging_text_en: String? = null
)

/**
 * DTO para nutriments conforme OpenFoodFacts (campos comuns).
 * Adicione campos extras conforme necessidade.
 */
data class NutrimentsApi(
    @SerializedName("sugars_100g") val sugars_100g: Double? = null,
    @SerializedName("fat_100g") val fat_100g: Double? = null,
    @SerializedName("saturated-fat_100g") val saturated_fat_100g: Double? = null,
    @SerializedName("salt_100g") val salt_100g: Double? = null,
    @SerializedName("sodium_100g") val sodium_100g: Double? = null,
    @SerializedName("fiber_100g") val fiber_100g: Double? = null,
    @SerializedName("proteins_100g") val proteins_100g: Double? = null
)
