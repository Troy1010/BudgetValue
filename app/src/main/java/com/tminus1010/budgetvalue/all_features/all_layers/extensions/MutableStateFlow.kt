package com.tminus1010.budgetvalue.all_features.all_layers.extensions

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

fun <T> MutableStateFlow<T>.easyEmit(x: T) = runBlocking { emit(x) }
fun MutableStateFlow<Unit>.easyEmit() = easyEmit(Unit)
fun <T> MutableStateFlow<T>.onNext(x: T) = easyEmit(x)
fun MutableStateFlow<Unit>.onNext() = easyEmit(Unit)