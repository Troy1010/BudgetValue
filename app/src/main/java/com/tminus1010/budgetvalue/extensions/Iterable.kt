package com.tminus1010.budgetvalue.extensions

import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal


fun Iterable<BigDecimal>.sum(): BigDecimal {
    return this.fold(BigDecimal.ZERO, BigDecimal::add)
}

fun <T : Observable<BigDecimal>> Iterable<T>.total(): Observable<BigDecimal> {
    return Observable.fromIterable(this)
        .flatMap {
            it
                .startWithItem(BigDecimal.ZERO)
                .distinctUntilChanged()
                .pairwise()
                .map { it.second - it.first }
        }
        .scan(BigDecimal.ZERO, BigDecimal::add)
}

fun <T> Iterable<T>.pairwise(): Iterable<Pair<T, T>> {
    return this.zip(this.drop(1)) { a, b -> Pair(a, b) }
}

fun <T> Iterable<T>.startWith(item: T): Iterable<T> {
    return this.toMutableList().apply { add(0, item) }
}

fun <T> Iterable<T>.distinctUntilChangedBy(function: (T)->Any): Iterable<T> {
    return this
        .pairwise()
        .filter { function(it.first) != function(it.second) }
        .map { it.second }
        .toMutableList()
        .also { it.add(0, this.first()) }
}
