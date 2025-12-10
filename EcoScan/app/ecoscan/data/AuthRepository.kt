package com.ifpe.ecoscan.data

import android.content.Context
import com.ifpe.ecoscan.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class AuthRepository(private val context: Context, private val auth: FirebaseAuth = FirebaseAuth.getInstance()) {

    private object Keys {
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_AVATAR = stringPreferencesKey("user_avatar")
        val USER_VERIFIED = booleanPreferencesKey("user_verified")
    }

    // --- Firebase operations ---
    suspend fun loginWithEmail(email: String, password: String): FirebaseUser? = withContext(Dispatchers.IO) {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            user?.let { saveUserLocally(it) }
            user
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        auth.signOut()
        clearLocalUser()
    }

    fun getCurrentFirebaseUser(): User? {
        val u = auth.currentUser ?: return null
        return mapFirebaseToUser(u)
    }

    // --- DataStore local ---
    private val dataStore = context.dataStore

    private suspend fun saveUserLocally(firebaseUser: FirebaseUser) {
        dataStore.edit { prefs ->
            prefs[Keys.USER_ID] = firebaseUser.uid ?: ""
            prefs[Keys.USER_NAME] = firebaseUser.displayName ?: ""
            prefs[Keys.USER_EMAIL] = firebaseUser.email ?: ""
            prefs[Keys.USER_AVATAR] = firebaseUser.photoUrl?.toString() ?: ""
            prefs[Keys.USER_VERIFIED] = firebaseUser.isEmailVerified
        }
    }

    private suspend fun clearLocalUser() {
        dataStore.edit { prefs ->
            prefs.remove(Keys.USER_ID)
            prefs.remove(Keys.USER_NAME)
            prefs.remove(Keys.USER_EMAIL)
            prefs.remove(Keys.USER_AVATAR)
            prefs.remove(Keys.USER_VERIFIED)
        }
    }

    suspend fun getLocalUser(): User? = withContext(Dispatchers.IO) {
        val prefs = dataStore.data.map { it }.first()
        val id = prefs[Keys.USER_ID]
        if (id.isNullOrBlank()) return@withContext null
        User(
            id = id,
            name = prefs[Keys.USER_NAME],
            email = prefs[Keys.USER_EMAIL],
            avatarUrl = prefs[Keys.USER_AVATAR],
            verified = prefs[Keys.USER_VERIFIED] ?: false
        )
    }

    /**
     * Novo: tenta retornar o usuário logado (prefere local, senão firebase).
     */
    suspend fun getLoggedUser(): User? = withContext(Dispatchers.IO) {
        // 1) tenta local
        val local = getLocalUser()
        if (local != null) return@withContext local

        // 2) fallback: firebase
        val firebaseUser = auth.currentUser
        return@withContext firebaseUser?.let { mapFirebaseToUser(it) }
    }

    // --- utilitários ---
    private fun mapFirebaseToUser(firebaseUser: FirebaseUser): User {
        return User(
            id = firebaseUser.uid,
            name = firebaseUser.displayName,
            email = firebaseUser.email,
            avatarUrl = firebaseUser.photoUrl?.toString(),
            verified = firebaseUser.isEmailVerified
        )
    }
}
