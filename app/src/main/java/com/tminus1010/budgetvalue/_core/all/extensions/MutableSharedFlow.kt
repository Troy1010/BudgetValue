package com.tminus1010.budgetvalue._core.all.extensions

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking

fun <T> MutableSharedFlow<T>.easyEmit(t: T) {
    val flow = this
    runBlocking { flow.emit(t) }
}