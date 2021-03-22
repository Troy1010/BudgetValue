package com.tminus1010.budgetvalue.middleware.source_objects

data class AdditionOrRemoval<K, V>(
    val type: AdditionOrRemovalType,
    val key: K,
    val value: V,
)