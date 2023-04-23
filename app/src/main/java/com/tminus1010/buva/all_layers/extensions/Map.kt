package com.tminus1010.buva.all_layers.extensions

import com.tminus1010.buva.all_layers.source_objects.SourceHashMap

fun <K, V : Any> Map<K, V>.toSourceHashMap(exitValue: V): SourceHashMap<K, V> {
    return SourceHashMap(this, exitValue)
}

fun <K, V> Map<K, V>.copy(vararg keyValues: Pair<K, V>): Map<K, V> {
    return mapOf(
        *this.map { it.key to it.value }.toTypedArray(),
        *keyValues
    )
}

// For sorted maps, k in map sometimes fails.
// This is more reliable, but less performant.
fun <K, V> Map<K, V>.reliableContains(k: K): Boolean {
    return entries.any { k == it.key }
}

fun <K, V> Map<K, V>.withIndex(): Map<IndexedValue<K>, V> {
    return keys.withIndex().zip(values).associate { it }
}

