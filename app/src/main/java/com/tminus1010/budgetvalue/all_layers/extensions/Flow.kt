package com.tminus1010.budgetvalue.all_layers.extensions

import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.rx3.asObservable


fun <T : Any> Flow<T?>.asObservable2(): Observable<T> {
    return filterNotNull().asObservable()
}

fun <T> Flow<T>.takeUntilSignal(signal: Flow<Any>): Flow<T> = flow {
    try {
        coroutineScope {
            launch {
                signal.take(1).collect()
                this@coroutineScope.cancel()
            }
            collect {
                emit(it)
            }
        }
    } catch (e: CancellationException) {
        //ignore
    }
}