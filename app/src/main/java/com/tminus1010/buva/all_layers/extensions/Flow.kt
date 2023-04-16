package com.tminus1010.buva.all_layers.extensions

import com.tminus1010.buva.all_layers.source_objects.SourceMap
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx3.asObservable
import kotlinx.coroutines.withContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * This is a convenience method.
 *
 * It is very similar to .stateIn(), except you can use .first() and only get the default value if it were null.
 */
fun <T> Flow<T?>.shareInWithDefault(scope: CoroutineScope, started: SharingStarted, defaultValue: T): Flow<T> {
    return map { it ?: defaultValue }.distinctUntilChanged().shareIn(scope, started, 1)
}


fun <T : Any> Flow<T?>.asObservable2(): Observable<T> {
    return filterNotNull().asObservable()
}

fun <T> Flow<T>.throttleFist(windowDuration: Long): Flow<T> = flow {
    var windowStartTime = System.currentTimeMillis()
    var emitted = false
    collect { value ->
        val currentTime = System.currentTimeMillis()
        val delta = currentTime - windowStartTime
        if (delta >= windowDuration) {
            windowStartTime += delta / windowDuration * windowDuration
            emitted = false
        }
        if (!emitted) {
            emit(value)
            emitted = true
        }
    }
}

fun <K, V : Any, T : Any> Flow<Map<K, V>>.flatMapSourceMap(sourceMap: SourceMap<K, V>, outputChooser: (SourceMap<K, V>) -> Flow<T>): Flow<T> =
    channelFlow {
        launch {
            outputChooser(sourceMap)
                .collect { send(it) }
        }
        withContext(EmptyCoroutineContext) {
            this@flatMapSourceMap
                .collect { sourceMap.adjustTo(it) }
        }
    }
