package com.example.budgetvalue.extensions

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
