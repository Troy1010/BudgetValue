package com.tminus1010.budgetvalue.all_features.data.repo

import com.tminus1010.budgetvalue.all_features.data.service.SharedPrefWrapper
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class AppInitRepo @Inject constructor(
    private val sharedPrefWrapper: SharedPrefWrapper
) {
    fun isAppInitialized(): Boolean =
        sharedPrefWrapper.isAppInitialized()

    suspend fun pushAppInitBool2(appInitBool: Boolean) {
        sharedPrefWrapper.pushAppInitBool(appInitBool)
            .subscribeOn(Schedulers.io())
            .blockingAwait()
    }
}