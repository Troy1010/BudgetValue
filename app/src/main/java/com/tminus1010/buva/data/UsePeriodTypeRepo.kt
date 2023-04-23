package com.tminus1010.buva.data

import com.tminus1010.buva.data.easy_data_store.EasyDataStore
import com.tminus1010.buva.data.easy_data_store.EasyDataStoreFactory
import com.tminus1010.buva.data.easy_data_store.create
import com.tminus1010.buva.domain.UsePeriodType
import javax.inject.Inject

class UsePeriodTypeRepo @Inject constructor(
    easyDataStoreFactory: EasyDataStoreFactory,
) : EasyDataStore<UsePeriodType> by easyDataStoreFactory.create(UsePeriodType.USE_CALENDAR_PERIODS)