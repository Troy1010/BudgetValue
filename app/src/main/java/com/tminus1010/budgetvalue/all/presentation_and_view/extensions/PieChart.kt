package com.tminus1010.budgetvalue.all.presentation_and_view.extensions

import com.github.mikephil.charting.charts.PieChart
import com.tminus1010.budgetvalue.all.presentation_and_view.models.PieChartVMItem

fun PieChart.bind(pieChartVMItem: PieChartVMItem) {
    pieChartVMItem.bind(this)
}