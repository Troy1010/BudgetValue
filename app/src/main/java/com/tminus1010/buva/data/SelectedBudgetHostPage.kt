package com.tminus1010.buva.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tminus1010.buva.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectedBudgetHostPage @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    private val key = stringPreferencesKey("SelectedBudgetHostPage")

    val flow =
        dataStore.data
            .mapNotNull { it[key]?.toIntOrNull() }
            .stateIn(GlobalScope, SharingStarted.Eagerly, R.id.planFrag)

    fun set(int: Int) {
        GlobalScope.launch { dataStore.edit { it[key] = int.toString() } }
    }
}