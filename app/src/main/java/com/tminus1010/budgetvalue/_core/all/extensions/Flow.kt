package com.tminus1010.budgetvalue._core.all.extensions

import com.tminus1010.tmcommonkotlin.core.logx
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.rx3.asObservable


fun <T : Any> Flow<T?>.asObservable2(): Observable<T> {
    return filterNotNull().asObservable()
}

inline fun <reified T> Flow<T>.doLogx(prefix: String? = null): Flow<T> {
    return onEach { it.logx(prefix) }
        .onCompletion { if (it == null) "Completed".logx(prefix) else logz("$prefix`Error:", it) }
}