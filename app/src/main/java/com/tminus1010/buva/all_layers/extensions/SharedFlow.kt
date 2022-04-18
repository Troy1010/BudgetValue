package com.tminus1010.buva.all_layers.extensions

import kotlinx.coroutines.flow.SharedFlow


val <T> SharedFlow<T>.value get() = replayCache.getOrNull(0)