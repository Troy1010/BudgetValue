package com.tminus1010.budgetvalue._core.data

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepo @Inject constructor(
    private val sharedPrefWrapper: SharedPrefWrapper,
) : IMainRepo {
    override fun fetchAppInitBool(): Boolean =
        sharedPrefWrapper.fetchAppInitBool()

    override fun pushAppInitBool(appInitBool: Boolean): Completable =
        sharedPrefWrapper.pushAppInitBool(appInitBool)
            .subscribeOn(Schedulers.io())
}