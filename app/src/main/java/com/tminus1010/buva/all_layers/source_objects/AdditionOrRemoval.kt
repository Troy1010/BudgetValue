package com.tminus1010.buva.all_layers.source_objects

data class AdditionOrRemoval<K, V>(
    val type: AddRemType,
    val key: K,
    val value: V,
)