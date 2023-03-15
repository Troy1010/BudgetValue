package com.tminus1010.buva.all_layers.extensions

import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry


fun <E : Any> List<E>.plusIfNotNull(x: E?): List<E> =
    this.run { if (x == null) this else plus(x) }

@JvmName(name = "plusIfNotNullNullable")
fun <E : Any?> List<E>.plusIfNotNull(x: E?): List<E> =
    this.run { if (x == null) this else plus(x) }

// TODO: There should be a more performant way to do this..
fun <T> List<T>.replaceFirst(predicate: (T) -> Boolean, new: T): List<T> {
    val indexToReplace = this.withIndex().find { predicate(it.value) }?.index ?: return this
    return this.withIndex().map { if (it.index == indexToReplace) new else it.value }
}

// TODO: There should be a more performant way to do this..
fun <T> List<T>.remove(predicate: (T) -> Boolean): List<T> {
    val indexToReplace = this.withIndex().find { predicate(it.value) }?.index ?: return this
    return this.withIndex().flatMap { if (it.index == indexToReplace) listOf() else listOf(it.value) }
}

fun List<Float>.toBarEntries() =
    withIndex().map { (i, f) -> BarEntry(i.toFloat(), f) }

fun List<Float>.toEntries() =
    withIndex().map { (i, f) -> Entry(i.toFloat(), f) }

@JvmName("duifghsildufhguisrhgilurhtguilrhtgiu")
fun List<Pair<Float, String>>.toBarEntries() =
    withIndex().map { (i, pair) -> BarEntry(i.toFloat(), pair.first, pair.second) }