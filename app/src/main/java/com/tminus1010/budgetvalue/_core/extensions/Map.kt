package com.tminus1010.budgetvalue._core.extensions

import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap

fun <K, V> Map<K, V>.toSourceHashMap(exitValue: V): SourceHashMap<K, V> {
    return SourceHashMap(this, exitValue)
}

fun <K, V> Map<K, V>.copy(vararg keyValues: Pair<K, V>): Map<K, V> {
    return mapOf(
        *this.map { it.key to it.value }.toTypedArray(),
        *keyValues
    )
}
