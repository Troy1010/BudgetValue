package com.tminus1010.budgetvalue.source_objects

data class Change<K, V>(
    val changeType: ChangeType,
    val key: K,
    val value: V
)