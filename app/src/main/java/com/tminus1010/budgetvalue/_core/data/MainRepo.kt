package com.tminus1010.budgetvalue._core.data

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
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

    override val anchorDateOffset: Observable<Long> =
        sharedPrefWrapper.anchorDateOffset
            .subscribeOn(Schedulers.io())

    override fun pushAnchorDateOffset(anchorDateOffset: Long?): Completable =
        sharedPrefWrapper.pushAnchorDateOffset(anchorDateOffset)
            .subscribeOn(Schedulers.io())

    override val blockSize: Observable<Long> =
        sharedPrefWrapper.blockSize
            .subscribeOn(Schedulers.io())

    override fun pushBlockSize(blockSize: Long?): Completable =
        sharedPrefWrapper.pushBlockSize(blockSize)
            .subscribeOn(Schedulers.io())
}