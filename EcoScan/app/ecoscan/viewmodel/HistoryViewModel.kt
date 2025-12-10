package com.ifpe.ecoscan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifpe.ecoscan.screens.ProductRepository   // IMPORT CORRIGIDO (usa o repo em data)
import com.ifpe.ecoscan.model.Product
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class HistoryUiState {
    object Loading : HistoryUiState()
    data class Success(val items: List<Product>) : HistoryUiState()
    object Empty : HistoryUiState()
    data class Error(val message: String) : HistoryUiState()
}

sealed class HistoryEvent {
    data class ShowToast(val message: String) : HistoryEvent()
}

class HistoryViewModel(private val repository: ProductRepository = ProductRepository) : ViewModel() {
    // Observe: ProductRepository is a singleton object, so we pass it without parentheses

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HistoryEvent>()
    val events = _events.asSharedFlow()

    init { loadHistory() }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.value = HistoryUiState.Loading
            try {
                val list = repository.getHistory()
                _uiState.value = if (list.isEmpty()) HistoryUiState.Empty else HistoryUiState.Success(list)
            } catch (e: Exception) {
                _uiState.value = HistoryUiState.Error("Erro ao carregar histórico")
                _events.emit(HistoryEvent.ShowToast("Falha ao carregar histórico"))
            }
        }
    }

    fun removeItem(product: Product) {
        viewModelScope.launch {
            repository.deleteFromHistory(product)
            _events.emit(HistoryEvent.ShowToast("Item removido"))
            loadHistory()
        }
    }

    // adiciona produto ao histórico (usado por ProductDetailScreen)
    fun addProduct(product: Product) {
        viewModelScope.launch {
            repository.saveToHistory(product)
            _events.emit(HistoryEvent.ShowToast("Adicionado ao histórico"))
            loadHistory()
        }
    }
}
