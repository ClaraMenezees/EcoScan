package com.ifpe.ecoscan.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ifpe.ecoscan.data.AuthRepository
import com.ifpe.ecoscan.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val loading: Boolean = true,
    val user: User? = null,
    val error: String? = null
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = AuthRepository(application.applicationContext)

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state

    init { loadProfile() }

    fun loadProfile() {
        viewModelScope.launch {
            _state.value = ProfileUiState(loading = true)
            try {
                val user = repo.getLoggedUser()
                _state.value = ProfileUiState(loading = false, user = user)
            } catch (e: Exception) {
                _state.value = ProfileUiState(loading = false, user = null, error = e.localizedMessage)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repo.logout()
            _state.value = ProfileUiState(loading = false, user = null)
        }
    }
}
