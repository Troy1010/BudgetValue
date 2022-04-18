package com.tminus1010.buva.all_layers.extensions

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking

fun <T> MutableSharedFlow<T>.easyEmit(x: T) = runBlocking { emit(x) }
fun MutableSharedFlow<Unit>.easyEmit() = easyEmit(Unit)
fun <T> MutableSharedFlow<T>.onNext(x: T) = easyEmit(x)
fun MutableSharedFlow<Unit>.onNext() = easyEmit(Unit)