package com.tminus1010.budgetvalue.all_layers.extensions

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.Subject

/**
 * The emission is in MILLISECONDS
 */
fun Completable.toDuration(): Single<Long> = Single.defer {
    var start: Long = 0
    this
        .doOnSubscribe { start = System.currentTimeMillis() }
        .toSingle { System.currentTimeMillis() - start }
}

fun Completable.divertErrors(errorSubject: Subject<Throwable>): Completable =
    this.doOnError { errorSubject.onNext(it) }.onErrorComplete()