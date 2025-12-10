package com.ifpe.ecoscan.screens

import com.ifpe.ecoscan.api.RetrofitInstance
import com.ifpe.ecoscan.model.Nutriments
import com.ifpe.ecoscan.model.NutrimentsApi
import com.ifpe.ecoscan.model.Product
import com.ifpe.ecoscan.model.ProductResponse
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * ProductRepository (singleton) — consulta API e mantém histórico em memória.
 * Faz o mapeamento DTO (ProductApi / NutrimentsApi) -> modelo de domínio (Product / Nutriments).
 */
object ProductRepository {

    private val history = mutableListOf<Product>()
    private val mutex = Mutex()

    suspend fun fetchProductFromApi(barcode: String): Product? {
        return try {
            val response: ProductResponse = RetrofitInstance.api.getProduct(barcode)
            val api = response.product ?: return null

            // mapeia NutrimentsApi -> Nutriments (domínio)
            val nutrimentsMapped: Nutriments? = api.nutriments?.let { na: NutrimentsApi ->
                Nutriments(
                    sugars100g = na.sugars_100g,
                    fat100g = na.fat_100g,
                    saturatedFat100g = na.saturated_fat_100g,
                    salt100g = na.salt_100g,
                    sodium100g = na.sodium_100g,
                    fiber100g = na.fiber_100g,
                    proteins100g = na.proteins_100g
                )
            }

            // mapeia ProductApi -> Product (domínio)
            val product = Product(
                id = api.id ?: barcode,
                name = api.product_name ?: api.generic_name ?: "Produto desconhecido",
                genericName = api.generic_name,
                brand = api.brands,
                nutritionGrade = api.nutrition_grade,       // ou nutrition_grade_fr conforme seu DTO
                ecoscoreGrade = api.ecoscore_grade,
                barcode = barcode,
                imageUrl = api.image_front_url,
                ingredientsText = api.ingredients_text,
                nutriments = nutrimentsMapped,
                packagingTags = api.packaging_tags,
                packagingTextEn = api.packaging_text_en
            )

            // salva no histórico (thread-safe)
            saveToHistory(product)

            product
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun saveToHistory(product: Product) {
        mutex.withLock {
            if (product.barcode != null && history.none { it.barcode == product.barcode }) {
                history.add(0, product)
            }
        }
    }

    suspend fun getHistory(): List<Product> {
        return mutex.withLock { history.toList() }
    }

    suspend fun deleteFromHistory(product: Product) {
        mutex.withLock { history.removeIf { it.barcode == product.barcode } }
    }

    suspend fun clearHistory() {
        mutex.withLock { history.clear() }
    }
}
