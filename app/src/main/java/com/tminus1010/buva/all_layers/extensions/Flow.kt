package com.tminus1010.buva.all_layers.extensions

import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.rx3.asObservable


fun <T : Any> Flow<T?>.asObservable2(): Observable<T> {
    return filterNotNull().asObservable()
}