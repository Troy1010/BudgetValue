package com.tminus1010.buva.data

import com.tminus1010.buva.data.service.SharedPrefWrapper
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class HasAppBeenInitializedRepo @Inject constructor(
    private val sharedPrefWrapper: SharedPrefWrapper
) {
    fun wasAppInitialized(): Boolean =
        sharedPrefWrapper.isAppInitialized()

    suspend fun pushAppInitBool2(appInitBool: Boolean) {
        sharedPrefWrapper.pushAppInitBool(appInitBool)
            .subscribeOn(Schedulers.io())
            .blockingAwait()
    }
}