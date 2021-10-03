package com.tminus1010.budgetvalue._core.framework.source_objects

data class Change<K, V>(
    val type: AddRemEditType,
    val key: K,
    val value: V
)