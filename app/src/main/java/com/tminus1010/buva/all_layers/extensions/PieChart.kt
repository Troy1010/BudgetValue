package com.tminus1010.buva.all_layers.extensions

import com.github.mikephil.charting.charts.PieChart
import com.tminus1010.buva.ui.all_features.view_model_item.PieChartVMItem

fun PieChart.bind(pieChartVMItem: PieChartVMItem) {
    pieChartVMItem.bind(this)
}