package com.tminus1010.budgetvalue.all_features.all_layers.extensions

import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx3.asObservable


fun <T : Any> Flow<T?>.asObservable2(): Observable<T> {
    return filterNotNull().asObservable()
}

fun <T : Any?> Flow<T>.easyStateIn(coroutineScope: CoroutineScope, initialValue: T): StateFlow<T> {
    return runBlocking { return@runBlocking this@easyStateIn.stateIn(coroutineScope, SharingStarted.Eagerly, initialValue) }
}