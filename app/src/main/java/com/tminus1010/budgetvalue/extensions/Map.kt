package com.tminus1010.budgetvalue.extensions

import com.tminus1010.budgetvalue.source_objects.SourceHashMap

fun <K, V> Map<K, V>.toSourceHashMap(exitValue: V): SourceHashMap<K, V> {
    return SourceHashMap(this, exitValue)
}

fun <K, V> Map<K, V>.toHashMap(): HashMap<K, V> {
    return HashMap(this)
}

fun <K, V, K2, V2> Map<K, V>.associate(action: (Map.Entry<K, V>) -> Pair<K2, V2>): Map<K2, V2> {
    return this.entries.associate(action)
}