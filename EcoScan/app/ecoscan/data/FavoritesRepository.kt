package com.ifpe.ecoscan.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "ecoscan_favs")

object FavoritesRepository {
    private val KEY_FAVS = stringSetPreferencesKey("fav_barcodes")

    fun getFavoritesFlow(context: Context) =
        context.dataStore.data.map { prefs -> prefs[KEY_FAVS] ?: emptySet() }

    suspend fun addFavorite(context: Context, barcode: String) {
        context.dataStore.edit { prefs ->
            val set = prefs[KEY_FAVS]?.toMutableSet() ?: mutableSetOf()
            set.add(barcode)
            prefs[KEY_FAVS] = set
        }
    }

    suspend fun removeFavorite(context: Context, barcode: String) {
        context.dataStore.edit { prefs ->
            val set = prefs[KEY_FAVS]?.toMutableSet() ?: mutableSetOf()
            set.remove(barcode)
            prefs[KEY_FAVS] = set
        }
    }
}
