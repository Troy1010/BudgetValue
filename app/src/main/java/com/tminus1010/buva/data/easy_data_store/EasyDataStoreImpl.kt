package com.tminus1010.buva.data.easy_data_store

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


class EasyDataStoreImpl<T>(
    private val dataStore: DataStore<Preferences>,
    private val defaultValue: T,
    clazz: Class<T>,
) : EasyDataStore<T> {
    private val adapter = moshi.adapter(clazz)
    private val key = stringPreferencesKey(Throwable().stackTrace[2].className)

    override val flow =
        dataStore.data
            .map { adapter.fromJson(it[key] ?: "null") }
            .easyShareIn(GlobalScope, SharingStarted.Eagerly, defaultValue)

    override fun setDefault() = set(defaultValue)
    override fun set(x: T) {
        GlobalScope.launch { dataStore.edit { it[key] = adapter.toJson(x) } }
    }
}