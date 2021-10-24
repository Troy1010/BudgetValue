package com.tminus1010.budgetvalue.__core_testing

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.reflect.jvm.isAccessible

class DatastoreInMemory : DataStore<Preferences> {
    private val _data = MutableStateFlow<Preferences>(MutablePreferences::class.constructors.first().apply { isAccessible = true }.call(mutableMapOf<Preferences.Key<*>, Any>(), false))
    private val mutex = Mutex()
    override val data: Flow<Preferences> = _data
    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences) = mutex.withLock {
        _data.value = transform.invoke(_data.value)
        _data.value
    }
}