package com.tminus1010.budgetvalue.all_layers.extensions

import com.tminus1010.budgetvalue.framework.observable.source_objects.SourceHashMap

fun <K, V: Any> Map<K, V>.toSourceHashMap(exitValue: V): SourceHashMap<K, V> {
    return SourceHashMap(this, exitValue)
}

fun <K, V> Map<K, V>.copy(vararg keyValues: Pair<K, V>): Map<K, V> {
    return mapOf(
        *this.map { it.key to it.value }.toTypedArray(),
        *keyValues
    )
}

