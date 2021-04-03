package com.tminus1010.budgetvalue._shared.date_period_getter.data

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface ISettingsRepo {
    val anchorDateOffset: Observable<Long>
    fun pushAnchorDateOffset(anchorDateOffset: Long?): Completable
    val blockSize: Observable<Long>
    fun pushBlockSize(blockSize: Long?): Completable
}
