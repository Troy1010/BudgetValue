package com.example.budgetvalue.extensions

import com.example.budgetvalue.SourceHashMap

fun <K, V> Map<K, V>.toSourceHashMap(): SourceHashMap<K, V> {
    // I'm not sure why .apply { putAll(this) } doesn't work here..
//    return SourceHashMap<K, V>().apply { putAll(this) }
    return SourceHashMap<K, V>().also { this.forEach { (k, v) -> it[k] = v } }
}

fun <K, V> Map<K, V>.toHashMap(): HashMap<K, V> {
    return HashMap(this)
}