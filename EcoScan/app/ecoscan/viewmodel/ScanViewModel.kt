package com.ifpe.ecoscan.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifpe.ecoscan.api.RetrofitInstance
import com.ifpe.ecoscan.model.Nutriments
import com.ifpe.ecoscan.model.Product
import com.ifpe.ecoscan.model.ProductResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class ScanUiState {
    object Idle : ScanUiState()
    object Loading : ScanUiState()
    data class Success(val product: Product) : ScanUiState()
    data class Error(val message: String) : ScanUiState()
}

sealed class ScanEffect {
    data class ShowToast(val message: String) : ScanEffect()
    data class NavigateToDetails(val barcode: String) : ScanEffect()
}

class ScanViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<ScanEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    /**
     * Busca produto na API e mapeia ProductApi/NutrimentsApi -> Product/Nutriments
     */
    fun fetchProduct(barcode: String) {
        val cleanBarcode = barcode.trim()

        if (cleanBarcode.isBlank()) {
            viewModelScope.launch {
                val msg = "Código de barras inválido"
                _uiState.value = ScanUiState.Error(msg)
                _effect.send(ScanEffect.ShowToast(msg))
            }
            return
        }

        viewModelScope.launch {
            _uiState.value = ScanUiState.Loading

            try {
                Log.d("ScanViewModel", "Buscando produto para código: $cleanBarcode")

                val response: ProductResponse = RetrofitInstance.api.getProduct(cleanBarcode)

                if (response.status == 1 && response.product != null) {
                    val api = response.product

                    // mapeia NutrimentsApi? -> Nutriments?
                    val nutrimentsMapped: Nutriments? = api.nutriments?.let { na ->
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

                    // mapeia ProductApi -> Product (modelo de domínio)
                    val mapped = Product(
                        id = api.id ?: cleanBarcode,
                        name = api.product_name ?: api.generic_name ?: "Produto desconhecido",
                        genericName = api.generic_name,
                        brand = api.brands,
                        nutritionGrade = api.nutrition_grade,
                        ecoscoreGrade = api.ecoscore_grade,
                        barcode = cleanBarcode,
                        imageUrl = api.image_front_url,
                        ingredientsText = api.ingredients_text,
                        nutriments = nutrimentsMapped,
                        packagingTags = api.packaging_tags,
                        packagingTextEn = api.packaging_text_en
                    )

                    _uiState.value = ScanUiState.Success(mapped)
                    _effect.send(ScanEffect.NavigateToDetails(cleanBarcode))

                } else {
                    val msg = "Produto não encontrado na base de dados."
                    _uiState.value = ScanUiState.Error(msg)
                    _effect.send(ScanEffect.ShowToast(msg))
                }

            } catch (e: Exception) {
                // Log completo para você ver no Logcat
                Log.e("ScanViewModel", "Erro ao buscar produto", e)

                val userMessage = when (e) {
                    is UnknownHostException -> "Sem conexão com a internet. Verifique sua rede."
                    is SocketTimeoutException -> "Servidor demorou para responder. Tente novamente."
                    is HttpException -> {
                        if (e.code() == 404) {
                            "Produto não encontrado (código 404)."
                        } else {
                            "Erro do servidor (${e.code()}). Tente novamente."
                        }
                    }
                    else -> "Erro inesperado ao buscar o produto. (${e.localizedMessage ?: "desconhecido"})"
                }

                _uiState.value = ScanUiState.Error(userMessage)
                _effect.send(ScanEffect.ShowToast(userMessage))
            }
        }
    }
}
