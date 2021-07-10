package com.tminus1010.budgetvalue._core.extensions

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

/**
 * The emission is in MILLISECONDS
 */
fun Completable.toDuration(): Single<Long> = Single.defer {
    var start: Long = 0
    this
        .doOnSubscribe { start = System.currentTimeMillis() }
        .toSingle { System.currentTimeMillis() - start }
}