package com.tminus1010.budgetvalue._core.extensions

import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap

fun <K, V> Map<K, V>.toSourceHashMap(exitValue: V): SourceHashMap<K, V> {
    return SourceHashMap(this, exitValue)
}