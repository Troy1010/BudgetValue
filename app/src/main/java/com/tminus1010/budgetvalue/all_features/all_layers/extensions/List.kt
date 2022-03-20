package com.tminus1010.budgetvalue.all_features.all_layers.extensions


fun <E : Any> List<E>.plusIfNotNull(x: E?): List<E> =
    this.run { if (x == null) this else plus(x) }

@JvmName(name = "plusIfNotNullNullable")
fun <E : Any?> List<E>.plusIfNotNull(x: E?): List<E> =
    this.run { if (x == null) this else plus(x) }