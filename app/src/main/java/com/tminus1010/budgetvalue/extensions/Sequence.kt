package com.tminus1010.budgetvalue.extensions


fun <T> Sequence<T>.pairwise(): Sequence<Pair<T, T>> {
    return this.zip(this.drop(1)) { a, b -> Pair(a, b) }
}