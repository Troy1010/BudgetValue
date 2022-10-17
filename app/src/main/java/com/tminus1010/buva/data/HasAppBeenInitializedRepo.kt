package com.tminus1010.buva.data

import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class HasAppBeenInitializedRepo @Inject constructor(
    private val sharedPrefWrapper: SharedPrefWrapper
) {
    fun wasAppInitialized(): Boolean =
        sharedPrefWrapper.isAppInitialized()

    suspend fun pushAppInitBool(appInitBool: Boolean) {
        sharedPrefWrapper.pushAppInitBool(appInitBool)
            .subscribeOn(Schedulers.io())
            .blockingAwait()
    }
}