package com.example.budgetvalue.extensions

import com.tminus1010.tmcommonkotlin.logz.logz
import com.tminus1010.tmcommonkotlin.tuple.Box
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction


fun <T : Any> Observable<T>.pairwise(initialValue: T): Observable<Pair<T, T>> {
    return this
        .startWithItem(initialValue)
        .pairwise()
}

fun <T : Any> Observable<T>.pairwise(): Observable<Pair<T, T>> {
    return this
        .compose {
            it
                .skip(1)
                .zipWith(it, BiFunction<T, T, Pair<T, T>> { a, b -> Pair(b, a) })
        }
}

fun <T : Any, G : Any> Observable<T>.withLatestFrom(x: Observable<G>): Observable<Pair<T, G>> {
    return this.withLatestFrom(x, BiFunction<T, G, Pair<T, G>> { t1: T, t2: G -> Pair(t1, t2) })
}

fun <T : Any, G : Any, R:Any> Observable<T>.withLatestFrom(x: Observable<G>, action: (T, G)->R): Observable<R> {
    return this.withLatestFrom(x, BiFunction<T, G, R> { t1: T, t2: G -> action(t1, t2) })
}

fun <T> Observable<Box<T?>>.unbox(): Observable<T> {
    return this
        .filter { it.first != null }
        .map { it.first!! }
}

fun <T:Any> Observable<T>.logzz(msgPrefix:String, toDisplayable:(T)->Any = { it }): Observable<T> {
    return this
        .doOnNext { logz("$msgPrefix`${toDisplayable(it)}") }
}

fun <T> Observable<T>.startWith(item: T): Observable<T> {
    return this
        .startWithItem(item)
}
