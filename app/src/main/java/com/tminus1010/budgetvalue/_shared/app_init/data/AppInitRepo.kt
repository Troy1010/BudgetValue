package com.tminus1010.budgetvalue._shared.app_init.data

import com.tminus1010.budgetvalue._core.data.SharedPrefWrapper
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class AppInitRepo @Inject constructor(
    private val sharedPrefWrapper: SharedPrefWrapper
) {
    fun fetchAppInitBool(): Boolean =
        sharedPrefWrapper.fetchAppInitBool()

    fun pushAppInitBool(appInitBool: Boolean): Completable =
        sharedPrefWrapper.pushAppInitBool(appInitBool)
            .subscribeOn(Schedulers.io())
}