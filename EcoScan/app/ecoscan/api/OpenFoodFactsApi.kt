// arquivo: app/src/main/java/com/ifpe/ecoscan/api/OpenFoodFactsApi.kt
package com.ifpe.ecoscan.api

import com.ifpe.ecoscan.model.ProductResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenFoodFactsApi {

    @GET("api/v2/product/{barcode}")
    suspend fun getProduct(
        @Path("barcode") barcode: String,
        @Query("fields") fields: String =
            "product_name,brands,image_front_url,ecoscore_grade,ingredients_text," +
                    "nutriments_sugars_100g,nutriments_sodium_100g," +
                    "nutriments_fat_100g,nutriments_saturated_fat_100g," +
                    "nutriments_fiber_100g,nutriments_proteins_100g"
    ): ProductResponse
}
