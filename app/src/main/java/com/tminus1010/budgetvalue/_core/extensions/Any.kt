package com.tminus1010.budgetvalue.extensions

import io.reactivex.rxjava3.core.Observable

fun <T> T.toObservable() = Observable.just(this)