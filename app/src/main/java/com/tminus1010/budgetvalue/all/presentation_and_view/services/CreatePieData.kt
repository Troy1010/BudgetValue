package com.tminus1010.budgetvalue.all.presentation_and_view.services

import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.tminus1010.budgetvalue._core.app.CategoryAmounts
import kotlin.math.absoluteValue


fun createPieData(categoryAmounts: CategoryAmounts, title: String? = null, colors: List<Int>): PieData {
    return PieDataSet(categoryAmounts.map { PieEntry(it.value.toFloat().absoluteValue, it.key.name) }, title).apply {
        setDrawValues(false)
        sliceSpace = 1f
        selectionShift = 2f
        this.colors = colors
    }.let(::PieData).also {
        logz("it.dataSet.getColor(0):${it.dataSet.getColor(0)} to ${it.dataSet.getEntryForIndex(0).label}")
        logz("it.dataSet.getColor(1):${it.dataSet.getColor(1)} to ${it.dataSet.getEntryForIndex(1).label}")
        logz("it.dataSet.getColor(2):${it.dataSet.getColor(2)} to ${it.dataSet.getEntryForIndex(2).label}")
    }
}