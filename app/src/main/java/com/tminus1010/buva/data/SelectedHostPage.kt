package com.tminus1010.buva.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.extensions.easyShareIn
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectedHostPage @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    private val key = stringPreferencesKey("SelectedHostPage")
    private val defaultValue = R.id.importHostFrag

    val flow =
        dataStore.data
            .map { it[key]?.toIntOrNull() }
            .easyShareIn(GlobalScope, SharingStarted.Eagerly, defaultValue)

    fun setDefault() = set(defaultValue)
    fun set(int: Int) {
        GlobalScope.launch { dataStore.edit { it[key] = int.toString() } }
    }
}