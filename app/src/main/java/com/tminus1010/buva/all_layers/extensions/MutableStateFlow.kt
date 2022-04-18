package com.tminus1010.buva.all_layers.extensions

import kotlinx.coroutines.flow.MutableStateFlow

fun <T> MutableStateFlow<T>.easyEmit(x: T) {
    value = x
}

fun <T> MutableStateFlow<T>.onNext(x: T) = easyEmit(x)