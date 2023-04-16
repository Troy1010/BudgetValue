package com.tminus1010.buva.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tminus1010.buva.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectedReviewHostPage @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    private val key = stringPreferencesKey("SelectedReviewHostPage")
    private val defaultValue = R.id.spendPieChart

    val flow =
        dataStore.data
            .map { it[key]?.toIntOrNull() ?: defaultValue }
            .distinctUntilChanged()
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1) // not using stateIn bc it means .first() doesn't wait.

    fun setDefault() = set(defaultValue)
    fun set(int: Int) {
        GlobalScope.launch { dataStore.edit { it[key] = int.toString() } }
    }
}