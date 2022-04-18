package com.tminus1010.buva.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tminus1010.buva.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectedPage @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    private val key = stringPreferencesKey("SelectedPage")

    val flow =
        dataStore.data
            .map { it[key]?.toIntOrNull() ?: R.id.importFrag }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    fun set(int: Int) {
        GlobalScope.launch { dataStore.edit { it[key] = int.toString() } }
    }
}