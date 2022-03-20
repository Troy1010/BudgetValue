package com.tminus1010.budgetvalue.all.presentation.extensions

import com.github.mikephil.charting.charts.PieChart
import com.tminus1010.budgetvalue.all_features.presentation.model.PieChartVMItem

fun PieChart.bind(pieChartVMItem: PieChartVMItem) {
    pieChartVMItem.bind(this)
}