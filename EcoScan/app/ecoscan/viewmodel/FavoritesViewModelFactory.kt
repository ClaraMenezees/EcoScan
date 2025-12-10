package com.ifpe.ecoscan.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory para criar FavoritesViewModel (que exige Context).
 * Usa comparação direta com FavoritesViewModel::class.java para evitar
 * problemas de inferência genérica.
 */
class FavoritesViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // comparação direta mais segura do que isAssignableFrom em alguns setups
        return when {
            modelClass == FavoritesViewModel::class.java ->
                FavoritesViewModel(context) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
