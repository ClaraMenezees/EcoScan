package com.ifpe.ecoscan.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifpe.ecoscan.data.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel simples para favoritos (usa DataStore via FavoritesRepository).
 * Recebe Context no construtor (por isso precisamos de uma Factory).
 */
class FavoritesViewModel(private val context: Context) : ViewModel() {

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    fun observeFavorites() {
        viewModelScope.launch {
            FavoritesRepository.getFavoritesFlow(context).collect { set ->
                _favorites.value = set
            }
        }
    }

    fun toggleFavorite(barcode: String) {
        viewModelScope.launch {
            if (_favorites.value.contains(barcode)) {
                FavoritesRepository.removeFavorite(context, barcode)
            } else {
                FavoritesRepository.addFavorite(context, barcode)
            }
        }
    }
}

