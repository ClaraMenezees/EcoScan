package com.ifpe.ecoscan.screens

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ifpe.ecoscan.model.Product
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "history_store")

class HistoryRepository(private val context: Context) {

    private val gson = Gson()
    private val HISTORY_KEY = stringPreferencesKey("history_list")

    val historyFlow = context.dataStore.data.map { prefs ->
        val json = prefs[HISTORY_KEY] ?: "[]"
        val type = object : TypeToken<List<Product>>() {}.type
        gson.fromJson<List<Product>>(json, type)
    }

    suspend fun add(product: Product) {
        context.dataStore.edit { prefs ->
            val json = prefs[HISTORY_KEY] ?: "[]"
            val type = object : TypeToken<MutableList<Product>>() {}.type
            val list: MutableList<Product> = gson.fromJson(json, type)
            list.add(0, product)
            prefs[HISTORY_KEY] = gson.toJson(list)
        }
    }

    suspend fun clear() {
        context.dataStore.edit { prefs ->
            prefs[HISTORY_KEY] = "[]"
        }
    }
}
