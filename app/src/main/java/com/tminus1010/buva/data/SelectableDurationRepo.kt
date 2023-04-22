package com.tminus1010.buva.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.tminus1010.buva.domain.SelectableDuration
import com.tminus1010.buva.environment.adapter.MoshiProvider.moshi
import javax.inject.Inject

class SelectableDurationRepo @Inject constructor(
    dataStore: DataStore<Preferences>,
) : EasyDataStore<SelectableDuration>(
    dataStore,
    SelectableDuration.BY_MONTH,
    moshi.adapter(SelectableDuration::class.java),
)