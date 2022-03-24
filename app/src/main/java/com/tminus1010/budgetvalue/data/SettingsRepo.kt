package com.tminus1010.budgetvalue.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepo @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    private val anchorDateOffsetKey = stringPreferencesKey("anchorDateOffset")
    private val blockSizeKey = stringPreferencesKey("blockSize")

    val anchorDateOffset =
        dataStore.data
            .map { it[anchorDateOffsetKey]?.toLong() ?: ANCHOR_DATE_OFFSET_DEFAULT }
            .distinctUntilChanged()
            .stateIn(GlobalScope, SharingStarted.Eagerly, ANCHOR_DATE_OFFSET_DEFAULT)

    suspend fun pushAnchorDateOffset(anchorDateOffset: Long?) {
        dataStore.edit {
            if (anchorDateOffset == null)
                it.remove(anchorDateOffsetKey)
            else
                it[anchorDateOffsetKey] = anchorDateOffset.toString()
        }
    }

    val blockSize =
        dataStore.data
            .map { it[blockSizeKey]?.toLong() ?: BLOCK_SIZE_DEFAULT }
            .distinctUntilChanged()
            .stateIn(GlobalScope, SharingStarted.Eagerly, BLOCK_SIZE_DEFAULT)

    suspend fun pushBlockSize(anchorDateOffset: Long?) {
        dataStore.edit {
            if (anchorDateOffset == null)
                it.remove(blockSizeKey)
            else
                it[blockSizeKey] = anchorDateOffset.toString()
        }
    }

    companion object {
        private const val ANCHOR_DATE_OFFSET_DEFAULT: Long = 0
        private const val BLOCK_SIZE_DEFAULT: Long = 14
    }
}