package com.ifpe.ecoscan.utils

object IngredientAnalysis {

    private val riskyList = listOf(
        "corante", "corante caramelo", "caramelo iv",
        "conservante", "xarope de glicose", "açúcar invertido",
        "aspartame", "sucralose", "benzoato", "glutamato",
        "aromatizante artificial", "acidulante"
    )

    fun analyzeIngredients(ingredients: String?): List<String> {
        if (ingredients.isNullOrBlank()) return emptyList()

        val formatted = ingredients.lowercase()

        return riskyList.filter { formatted.contains(it) }
    }
}
