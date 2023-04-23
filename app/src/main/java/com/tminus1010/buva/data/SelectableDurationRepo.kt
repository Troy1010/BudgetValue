package com.tminus1010.buva.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.tminus1010.buva.domain.SelectableDuration
import javax.inject.Inject

class SelectableDurationRepo @Inject constructor(
    dataStore: DataStore<Preferences>,
) : EasyDataStore<SelectableDuration>(
    dataStore,
    SelectableDuration.BY_MONTH,
    SelectableDuration::class.java,
)