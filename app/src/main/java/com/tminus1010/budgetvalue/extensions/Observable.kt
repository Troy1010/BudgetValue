package com.tminus1010.budgetvalue.extensions

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

fun <T> Observable<T>.io(): Observable<T> = observeOn(Schedulers.io())
fun <T> Observable<T>.launch(scheduler: Scheduler = Schedulers.io(), completableProvider: (T) -> Completable): Disposable =
    this.observeOn(scheduler).flatMapCompletable { completableProvider(it) }.subscribe()