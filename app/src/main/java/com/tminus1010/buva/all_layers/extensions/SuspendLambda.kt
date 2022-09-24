package com.tminus1010.buva.all_layers.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


fun <R> (suspend () -> R).observe(coroutineScope: CoroutineScope) {
    coroutineScope.launch { this@observe.invoke() }
}