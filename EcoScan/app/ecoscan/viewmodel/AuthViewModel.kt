package com.ifpe.ecoscan.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class AuthUiState {
    object LoggedOut : AuthUiState()
    object Loading : AuthUiState()
    data class LoggedIn(val uid: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _state = MutableStateFlow<AuthUiState>(AuthUiState.LoggedOut)
    val state: StateFlow<AuthUiState> = _state

    init {
        auth.currentUser?.let { user ->
            _state.value = AuthUiState.LoggedIn(user.uid)
        }
    }

    fun login(email: String, password: String) {

        if (email.isBlank() || password.isBlank()) {
            _state.value = AuthUiState.Error("Preencha email e senha.")
            return
        }

        _state.value = AuthUiState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: ""
                _state.value = AuthUiState.LoggedIn(uid)
            }
            .addOnFailureListener { e ->
                val message = when {
                    e.localizedMessage?.contains("password") == true ->
                        "Senha incorreta!"
                    e.localizedMessage?.contains("no user record") == true ->
                        "Usuário não encontrado."
                    else -> e.localizedMessage ?: "Erro ao fazer login."
                }

                _state.value = AuthUiState.Error(message)
            }
    }

    fun signup(email: String, password: String) {

        if (email.isBlank() || password.isBlank()) {
            _state.value = AuthUiState.Error("Preencha email e senha.")
            return
        }

        _state.value = AuthUiState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: ""
                _state.value = AuthUiState.LoggedIn(uid)
            }
            .addOnFailureListener { e ->
                _state.value = AuthUiState.Error(
                    e.localizedMessage ?: "Erro ao criar conta."
                )
            }
    }

    fun logout() {
        auth.signOut()
        _state.value = AuthUiState.LoggedOut
    }
}
