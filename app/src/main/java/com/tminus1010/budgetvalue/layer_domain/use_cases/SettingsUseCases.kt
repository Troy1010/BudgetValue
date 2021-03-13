package com.tminus1010.budgetvalue.layer_domain.use_cases

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface SettingsUseCases {
    val anchorDateOffset: Observable<Long>
    fun pushAnchorDateOffset(anchorDateOffset: Long?): Completable
    val blockSize: Observable<Long>
    fun pushBlockSize(blockSize: Long?): Completable
}