package com.tminus1010.budgetvalue._core.extensions

import io.reactivex.rxjava3.core.Observable

fun <T> T.toObservable() = Observable.just(this)