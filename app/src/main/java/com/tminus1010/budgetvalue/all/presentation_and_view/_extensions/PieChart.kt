package com.tminus1010.budgetvalue.all.presentation_and_view

import com.github.mikephil.charting.charts.PieChart
import com.tminus1010.budgetvalue.all.presentation_and_view._models.PieChartVMItem

fun PieChart.bind(pieChartVMItem: PieChartVMItem) {
    pieChartVMItem.bind(this)
}