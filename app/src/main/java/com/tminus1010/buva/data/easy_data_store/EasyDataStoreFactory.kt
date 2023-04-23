package com.tminus1010.buva.data.easy_data_store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import javax.inject.Inject

class EasyDataStoreFactory @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    fun <T> _create(defaultValue: T, clazz: Class<T>): EasyDataStore<T> {
        return EasyDataStoreImpl(
            dataStore,
            defaultValue,
            clazz,
        )
    }
}

inline fun <reified T> EasyDataStoreFactory.create(defaultValue: T): EasyDataStore<T> {
    return _create(defaultValue, T::class.java)
}