package com.tminus1010.budgetvalue._core.all.extensions

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.tminus1010.tmcommonkotlin.core.logx
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx3.asObservable


fun <T : Any> Flow<T?>.asObservable2(): Observable<T> {
    return filterNotNull().asObservable()
}

inline fun <reified T> Flow<T>.doLogx(prefix: String? = null): Flow<T> {
    return onEach { it.logx(prefix) }
        .onCompletion { if (it == null) "Completed".logx(prefix) else logz("$prefix`Error:", it) }
}

inline fun <reified T> Flow<T>.observe(lifecycleOwner: LifecycleOwner, crossinline lambda: suspend (T) -> Unit) {
    val flow = this
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect { lambda(it) }
        }
    }
}

fun <T : Any?> Flow<T>.easyStateIn(coroutineScope: CoroutineScope, initialValue: T): StateFlow<T> {
    return runBlocking { return@runBlocking this@easyStateIn.stateIn(coroutineScope, SharingStarted.Eagerly, initialValue) }
}