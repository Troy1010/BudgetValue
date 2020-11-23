package com.example.budgetvalue.extensions

import com.example.budgetvalue.SourceHashMap

fun <K, V> Map<K, V>.toSourceHashMap(): SourceHashMap<K, V> {
    val returning = SourceHashMap<K, V>()
    returning.putAll(this)
    return returning
}