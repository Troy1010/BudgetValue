package com.tminus1010.budgetvalue.all_features.all_layers.extensions

import kotlinx.coroutines.flow.SharedFlow


val <T> SharedFlow<T>.value get() = replayCache.getOrNull(0)