package com.tminus1010.budgetvalue.extensions

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

fun <T> Observable<T>.io() = observeOn(Schedulers.io())