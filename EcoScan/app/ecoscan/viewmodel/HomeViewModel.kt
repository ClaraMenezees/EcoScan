package com.ifpe.ecoscan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifpe.ecoscan.model.Product
import com.ifpe.ecoscan.screens.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val loading: Boolean = false,
    val greeting: String = "Bem-vindo",
    val historyCount: Int = 0,
    val lastProduct: Product? = null,
    val error: String? = null
)

class HomeViewModel(
    private val repository: ProductRepository = ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(loading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadSummary()
    }

    fun loadSummary() {
        viewModelScope.launch {
            try {
                val list = repository.getHistory()
                val count = list.size
                val last = list.firstOrNull()
                val greeting = if (count > 0) "Bem-vindo de volta 🌿" else "Bem-vindo ao EcoScan 🌿"
                _uiState.value = HomeUiState(
                    loading = false,
                    greeting = greeting,
                    historyCount = count,
                    lastProduct = last,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState(
                    loading = false,
                    greeting = "Bem-vindo",
                    historyCount = 0,
                    lastProduct = null,
                    error = "Erro ao carregar resumo"
                )
            }
        }
    }
}
