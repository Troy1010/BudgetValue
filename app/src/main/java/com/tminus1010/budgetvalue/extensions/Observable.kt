package com.tminus1010.budgetvalue.extensions

import com.tminus1010.tmcommonkotlin.logz.logz
import com.tminus1010.tmcommonkotlin.tuple.Box
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
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
                .zipWith(it) { a, b -> Pair(b, a) }
        }
}

fun <T : Any, G : Any> Observable<T>.withLatestFrom(x: Observable<G>): Observable<Pair<T, G>> {
    return this.withLatestFrom(x, BiFunction { t1: T, t2: G -> Pair(t1, t2) })
}

fun <A : Any, B : Any, C: Any> Observable<A>.withLatestFrom(b: Observable<B>, c: Observable<C>): Observable<Triple<A, B, C>> {
    return this.withLatestFrom(b, BiFunction { a: A, b: B -> Pair(a, b) })
        .withLatestFrom(c, BiFunction { pair:Pair<A, B>, c: C -> Triple(pair.first, pair.second, c) })
}

fun <T : Any, G : Any, R:Any> Observable<T>.withLatestFrom(x: Observable<G>, action: (T, G)->R): Observable<R> {
    return this.withLatestFrom(x, BiFunction { t1: T, t2: G -> action(t1, t2) })
}

fun <T> Observable<Box<T?>>.unbox(): Observable<T> {
    return this
        .filter { it.first != null }
        .map { it.first!! }
}

fun <T> Observable<T>.logzz(msgPrefix:String, toDisplayable:((T)->Any)? = null): Observable<T> {
    return this
        .doOnNext { logz("$msgPrefix`${if (toDisplayable==null) it else toDisplayable(it)}") }
}

fun <T> Observable<T>.noEnd(): Observable<T> {
    return this
        .mergeWith(Observable.never())
}

fun <T> Observable<T>.box(): Observable<Box<T>> {
    return this
        .map { Box(it) }
}

fun <T> Observable<T>.boxNull(): Observable<Box<T?>> {
    return this
        .map { Box(it) }
}

fun <T> Observable<T>.boxStartNull(): Observable<Box<T?>> {
    return this
        .boxNull()
        .startWithItem(Box(null))
}

fun <T> Observable<T>.isCold(): Boolean {
    return this
        .toBehaviorSubject()
        .value != null
}


