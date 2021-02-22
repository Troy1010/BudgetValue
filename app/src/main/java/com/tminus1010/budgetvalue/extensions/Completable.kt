package com.tminus1010.budgetvalue.extensions

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

fun Completable.onIO() : Disposable {
    return this.subscribeOn(Schedulers.io()).subscribe()
}