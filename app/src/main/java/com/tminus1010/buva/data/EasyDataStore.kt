package com.tminus1010.buva.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.squareup.moshi.JsonAdapter
import com.tminus1010.buva.all_layers.extensions.easyShareIn
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


open class EasyDataStore<T> @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val defaultValue: T,
    private val adapter: JsonAdapter<T>,
) {
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