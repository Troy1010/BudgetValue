package com.example.budgetvalue.extensions

import com.example.budgetvalue.SourceHashMap

fun <K, V> Map<K, V>.toSourceHashMap(): SourceHashMap<K, V> {
    return SourceHashMap<K, V>().also { it.putAll(this) }
}

fun <K, V> Map<K, V>.toHashMap(): HashMap<K, V> {
    return HashMap(this)
}

fun <K, V, K2, V2> Map<K, V>.associate(action: (Map.Entry<K, V>) -> Pair<K2, V2>): Map<K2, V2> {
    return this.entries.associate(action)
}