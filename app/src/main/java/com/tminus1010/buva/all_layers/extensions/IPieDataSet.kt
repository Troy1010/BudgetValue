package com.tminus1010.buva.all_layers.extensions

import com.github.mikephil.charting.interfaces.datasets.IPieDataSet

val IPieDataSet.entries
    get() = (0 until entryCount).map { i -> getEntryForIndex(i) }