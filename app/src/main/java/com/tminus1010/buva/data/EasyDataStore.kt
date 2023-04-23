package com.tminus1010.buva.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tminus1010.buva.all_layers.extensions.easyShareIn
import com.tminus1010.buva.environment.adapter.MoshiProvider.moshi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


open class EasyDataStore<T>(
    private val dataStore: DataStore<Preferences>,
    private val defaultValue: T,
    clazz: Class<T>,
) {
    private val adapter = moshi.adapter(clazz)
    private val key = stringPreferencesKey(javaClass.name)

    val flow =
        dataStore.data
            .map { adapter.fromJson(it[key] ?: "null") }
            .easyShareIn(GlobalScope, SharingStarted.Eagerly, defaultValue)

    fun setDefault() = set(defaultValue)
    fun set(x: T) {
        GlobalScope.launch { dataStore.edit { it[key] = adapter.toJson(x) } }
    }
}