package com.tminus1010.budgetvalue._core.extensions

import com.tminus1010.budgetvalue._core.middleware.source_objects.SourceHashMap
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import java.math.BigDecimal

fun <K, V> Map<K, V>.toSourceHashMap(exitValue: V): SourceHashMap<K, V> {
    return SourceHashMap(this, exitValue)
}

fun <K, V> Map<K, V>.copy(vararg keyValues: Pair<K, V>): Map<K, V> {
    return mapOf(
        *this.map { it.key to it.value }.toTypedArray(),
        *keyValues
    )
}

