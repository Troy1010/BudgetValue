package com.tminus1010.budgetvalue._core.middleware.source_objects

data class Change<K, V>(
    val type: ChangeType,
    val key: K,
    val value: V
)