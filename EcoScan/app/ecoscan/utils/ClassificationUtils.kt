package com.ifpe.ecoscan.utils

import com.ifpe.ecoscan.model.Nutriments
import kotlin.math.roundToInt

data class ClassificationResult(
    val label: String,        // "Saudável", "Moderado", "Prejudicial", "Indefinido"
    val reason: String,       // explicação clara e legível para o usuário
    val confidence: Double    // 0.0 .. 1.0
)

object ClassificationUtils {

    private val blacklist = listOf(
        "high fructose corn syrup", "xarope de milho", "xarope de milho de alta frutose",
        "partially hydrogenated", "hidrogenado", "mono- and diglycerides",
        "sodium nitrite", "nitrito de sódio", "sulfite", "sulphite", "sulphites",
        "potassium sorbate", "sodium benzoate", "tbhq", "bht", "bha",
        "artificial flavor", "aroma artificial", "caramel color", "corante caramelo",
        "polysorbate", "sorbitan", "azodicarbonamide", "emulsifier", "emulsionante",
        "palm oil", "óleo de palma", "maltodextrin", "maltodextrina",
        "sucralose", "aspartame"
    )

    private val whitelist = listOf(
        "whole grain", "integral", "whole wheat", "aveia", "oat", "quinoa",
        "legume", "lentil", "fiber", "fibras"
    )

    /**
     * Classifica combinando fontes e produz um motivo descritivo não ambíguo.
     *
     * Observação: sodium100g pode estar em mg em algumas APIs; detectamos e convertemos se necessário.
     */
    fun classifyProduct(
        nutriments: Nutriments?,
        nutritionGradeRaw: String?,
        ecoscore: String?,
        ingredientsText: String?
    ): ClassificationResult {

        // normalize nutrition grade if present
        val nutritionGrade = nutritionGradeRaw?.trim()?.lowercase()

        // 1) Nutri-grade (se reconhecido, alta confiança)
        if (!nutritionGrade.isNullOrBlank()) {
            when (nutritionGrade) {
                "a", "a+" -> return ClassificationResult(
                    label = "Saudável",
                    reason = "Base: Nutri-grade ${nutritionGradeRaw?.uppercase()}. Produto classificado como saudável conforme a grade fornecida.",
                    confidence = 0.95
                )
                "b" -> return ClassificationResult(
                    label = "Saudável",
                    reason = "Base: Nutri-grade ${nutritionGradeRaw?.uppercase()}. Produto com boa avaliação nutricional.",
                    confidence = 0.90
                )
                "c" -> return ClassificationResult(
                    label = "Moderado",
                    reason = "Base: Nutri-grade ${nutritionGradeRaw?.uppercase()}. Atenção a alguns nutrientes críticos.",
                    confidence = 0.88
                )
                "d" -> return ClassificationResult(
                    label = "Prejudicial",
                    reason = "Base: Nutri-grade ${nutritionGradeRaw?.uppercase()}. Produto com avaliação nutricional desfavorável.",
                    confidence = 0.95
                )
                "e" -> return ClassificationResult(
                    label = "Prejudicial",
                    reason = "Base: Nutri-grade ${nutritionGradeRaw?.uppercase()}. Alto risco nutricional segundo a grade.",
                    confidence = 0.98
                )
                // se veio algo diferente (ex.: "unknown" ou outro texto), não retornamos aqui — vamos incluir esse dado no motivo final
            }
        }

        // guardaremos observações para compor o motivo final caso nutriments ou ingredients sejam usados
        val notes = mutableListOf<String>()
        if (!nutritionGrade.isNullOrBlank()) {
            notes.add("Grade nutricional fornecida: '${nutritionGradeRaw}'. Não foi possível mapear diretamente.")
        }

        // 2) Usa nutriments (preferível se disponível)
        if (nutriments != null) {
            val sugar = nutriments.sugars100g ?: 0.0
            val satFat = nutriments.saturatedFat100g ?: 0.0
            var sodium = nutriments.sodium100g ?: 0.0

            // detectar mg vs g
            if (sodium > 1000.0) sodium = sodium / 1000.0
            val sodiumMg = (sodium * 1000.0).roundToInt()

            // construir motivo descritivo com valores reais
            val parts = mutableListOf<String>()
            parts.add("Açúcar ${"%.1f".format(sugar)} g/100g")
            parts.add("Gord. saturada ${"%.1f".format(satFat)} g/100g")
            parts.add("Sódio ${sodiumMg} mg/100g")

            // score simples (mesma lógica anterior)
            val sugarScore = (sugar / 10.0).coerceAtMost(3.0)
            val satFatScore = (satFat / 2.0).coerceAtMost(3.0)
            val sodiumScore = (sodium / 0.3).coerceAtMost(3.0)
            val fiberBonus = (nutriments.fiber100g ?: 0.0) / 5.0
            val proteinBonus = (nutriments.proteins100g ?: 0.0) / 10.0

            val penalty = sugarScore * 0.45 + satFatScore * 0.35 + sodiumScore * 0.2
            val bonus = (fiberBonus + proteinBonus) * 0.8
            val rawScore = (penalty - bonus).coerceIn(0.0, 6.0)

            val classificationLabel: String
            val confidence: Double
            val reasonMain: String

            when {
                rawScore <= 1.2 -> {
                    classificationLabel = "Saudável"
                    confidence = 0.85
                    reasonMain = "Baixo em nutrientes críticos (${parts.joinToString(", ")})"
                }
                rawScore <= 3.0 -> {
                    classificationLabel = "Moderado"
                    confidence = 0.75
                    reasonMain = "Nível moderado de nutrientes críticos (${parts.joinToString(", ")})"
                }
                else -> {
                    classificationLabel = "Prejudicial"
                    confidence = 0.9
                    reasonMain = "Alto em nutrientes críticos (${parts.joinToString(", ")})"
                }
            }

            val notePhrase = if (notes.isNotEmpty()) " Observação: ${notes.joinToString("; ")}." else ""
            val reason = "$reasonMain.$notePhrase"

            return ClassificationResult(label = classificationLabel, reason = reason, confidence = confidence)
        }

        // 3) Ingredientes (fallback quando nutriments ausentes)
        if (!ingredientsText.isNullOrBlank()) {
            val lower = ingredientsText.lowercase()
            val foundBlacklist = blacklist.filter { lower.contains(it) }
            if (foundBlacklist.isNotEmpty()) {
                val reason = "Detectados ingredientes potencialmente nocivos: ${foundBlacklist.take(5).joinToString(", ")}." +
                        if (notes.isNotEmpty()) " Observação: ${notes.joinToString("; ")}." else ""
                return ClassificationResult("Prejudicial", reason, 0.85)
            }

            val foundWhite = whitelist.filter { lower.contains(it) }
            if (foundWhite.isNotEmpty()) {
                val reason = "Ingredientes positivos detectados: ${foundWhite.take(5).joinToString(", ")}." +
                        if (notes.isNotEmpty()) " Observação: ${notes.joinToString("; ")}." else ""
                return ClassificationResult("Saudável", reason, 0.6)
            }

            // não encontrou nada significativo nos ingredientes
            val reason = "Ingredientes informados, mas sem itens críticos detectados." +
                    if (notes.isNotEmpty()) " Observação: ${notes.joinToString("; ")}." else ""
            return ClassificationResult("Indefinido", reason, 0.35)
        }

        // 4) Ecoscore como última tentativa informativa
        if (!ecoscore.isNullOrBlank()) {
            val e = ecoscore.trim().lowercase()
            val reason = "Escore ambiental: ${ecoscore.uppercase()}." +
                    if (notes.isNotEmpty()) " Observação: ${notes.joinToString("; ")}." else ""
            return when (e) {
                "a", "b" -> ClassificationResult("Saudável", reason, 0.5)
                "c" -> ClassificationResult("Moderado", reason, 0.45)
                else -> ClassificationResult("Prejudicial", reason, 0.45)
            }
        }

        // 5) fallback final — informe claramente o que faltou
        val missing = mutableListOf<String>()
        if (nutritionGrade.isNullOrBlank()) missing.add("grade nutricional")
        if (nutriments == null) missing.add("informações nutricionais")
        if (ingredientsText.isNullOrBlank()) missing.add("ingredientes")
        val missingPhrase = if (missing.isNotEmpty()) "Faltam: ${missing.joinToString(", ")}." else ""
        val notePhrase = if (notes.isNotEmpty()) " Observação: ${notes.joinToString("; ")}." else ""

        val finalReason = "Dados insuficientes para classificação confiável. $missingPhrase$notePhrase Você pode classificar manualmente ou tentar outro produto."
        return ClassificationResult("Indefinido", finalReason, 0.15)
    }
}
