package com.tminus1010.budgetvalue.extensions

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

fun <T> Observable<T>.io() = observeOn(Schedulers.io())
fun <T> Observable<T>.launch(scheduler: Scheduler = Schedulers.io(), completableProvider: (T) -> Completable) =
    this.flatMapCompletable { completableProvider(it).observeOn(scheduler) }.subscribe()