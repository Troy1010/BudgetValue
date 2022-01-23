package com.tminus1010.budgetvalue._core.all.extensions

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx3.asObservable


fun <T : Any> Flow<T?>.asObservable2(): Observable<T> {
    return filterNotNull().asObservable()
}

inline fun <reified T> Flow<T>.observe(lifecycleOwner: LifecycleOwner, crossinline lambda: suspend (T) -> Unit) {
    val flow = this
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect { lambda(it) }
        }
    }
}

fun <T> Flow<T>.observe(coroutineScope: CoroutineScope, lambda: suspend (T) -> Unit) {
    val flow = this
    coroutineScope.launch { flow.collect { lambda(it) } }
}

fun <T : Any?> Flow<T>.easyStateIn(coroutineScope: CoroutineScope, initialValue: T): StateFlow<T> {
    return runBlocking { return@runBlocking this@easyStateIn.stateIn(coroutineScope, SharingStarted.Eagerly, initialValue) }
}