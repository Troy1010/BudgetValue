package com.example.budgetvalue.extensions

import io.reactivex.rxjava3.core.Observable


fun <T : Any> Observable<T>.pairwise(initialValue: T): Observable<Pair<T, T>> {
    return this
        .startWithItem(initialValue)
        .pairwise()
}

@Suppress("UNUSED_EXPRESSION")
fun <T : Any> Observable<T>.pairwise(): Observable<Pair<T, T>> {
    lateinit var lastValue: T
    return this
        .doOnNext { try { lastValue } catch (e: UninitializedPropertyAccessException) { lastValue = it } }
        .skip(1)
        .map { Pair(lastValue, it) }
        .doOnNext { lastValue = it.second }
}