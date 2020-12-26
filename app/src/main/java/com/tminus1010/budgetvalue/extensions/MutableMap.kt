package com.tminus1010.budgetvalue.extensions

fun <K, V> MutableMap<K, V>.removeIf(function: (Map.Entry<K, V>) -> Boolean) {
    this.toMap().asSequence()
        .filter(function)
        .forEach { this.remove(it.key) }
}