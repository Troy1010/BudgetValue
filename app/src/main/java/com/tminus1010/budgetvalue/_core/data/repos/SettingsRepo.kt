package com.tminus1010.budgetvalue._core.data.repos

import com.tminus1010.budgetvalue._core.data.SharedPrefWrapper
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepo @Inject constructor(
    private val sharedPrefWrapper: SharedPrefWrapper
) {
    val anchorDateOffset: Observable<Long> =
        sharedPrefWrapper.anchorDateOffset.subscribeOn(Schedulers.io())

    fun pushAnchorDateOffset(anchorDateOffset: Long?): Completable =
        sharedPrefWrapper.pushAnchorDateOffset(anchorDateOffset).subscribeOn(Schedulers.io())

    val blockSize: Observable<Long> =
        sharedPrefWrapper.blockSize.subscribeOn(Schedulers.io())

    fun pushBlockSize(blockSize: Long?): Completable =
        sharedPrefWrapper.pushBlockSize(blockSize).subscribeOn(Schedulers.io())
}