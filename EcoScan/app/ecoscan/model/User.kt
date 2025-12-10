package com.ifpe.ecoscan.model

/**
 * Modelo de domínio do usuário logado.
 * Adicionei `verified` e `token` como opcionais para compatibilidade.
 */
data class User(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val avatarUrl: String? = null,
    val verified: Boolean? = null,
    val token: String? = null
)
