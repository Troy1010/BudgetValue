package com.tminus1010.buva.data

import com.tminus1010.buva.data.easy_data_store.EasyDataStore
import com.tminus1010.buva.data.easy_data_store.EasyDataStoreFactory
import com.tminus1010.buva.data.easy_data_store.create
import com.tminus1010.buva.domain.SelectableDuration
import javax.inject.Inject

class SelectableDurationRepo @Inject constructor(
    easyDataStoreFactory: EasyDataStoreFactory,
) : EasyDataStore<SelectableDuration> by easyDataStoreFactory.create(SelectableDuration.BY_MONTH)