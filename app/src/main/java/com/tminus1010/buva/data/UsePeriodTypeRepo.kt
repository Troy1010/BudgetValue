package com.tminus1010.buva.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.tminus1010.buva.domain.UsePeriodType
import javax.inject.Inject

class UsePeriodTypeRepo @Inject constructor(
    dataStore: DataStore<Preferences>,
) : EasyDataStore<UsePeriodType>(
    dataStore,
    UsePeriodType.USE_CALENDAR_PERIODS,
    UsePeriodType::class.java,
)