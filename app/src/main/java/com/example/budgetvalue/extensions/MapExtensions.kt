package com.example.budgetvalue.extensions

import com.example.budgetvalue.SourceHashMap

fun <K, V> Map<K, V>.toSourceHashMap(): SourceHashMap<K, V> {
    return SourceHashMap<K, V>().also { it.putAll(this) }
}

fun <K, V> Map<K, V>.toHashMap(): HashMap<K, V> {
    return HashMap(this)
}