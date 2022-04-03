package com.tminus1010.budgetvalue.all_layers.extensions

import kotlinx.coroutines.flow.MutableStateFlow

fun <T> MutableStateFlow<T>.easyEmit(x: T) {
    value = x
}

fun MutableStateFlow<Unit>.easyEmit() = easyEmit(Unit)
fun <T> MutableStateFlow<T>.onNext(x: T) = easyEmit(x)
fun MutableStateFlow<Unit>.onNext() = easyEmit(Unit)