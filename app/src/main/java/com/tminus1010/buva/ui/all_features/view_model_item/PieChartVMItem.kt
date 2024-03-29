package com.tminus1010.buva.ui.all_features.view_model_item

import android.graphics.Color
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import kotlinx.coroutines.flow.Flow

class PieChartVMItem(
    private val pieData: Flow<PieData>,
    private val holeRadius: Float = 35f,
    private val transparentCircleRadius: Float = 40f,
    private val description: Description? = null,
    private val centerText: String? = null,
    private val centerTextSize: Float = 25f,
    private val isLegendEnabled: Boolean = false,
    private val entryLabelColor: Int = Color.BLACK,
    private val entryLabelTextSize: Float = 25f,
) {
    fun bind(pieChart: PieChart) {
        pieChart.holeRadius = holeRadius
        pieChart.transparentCircleRadius = transparentCircleRadius
        pieChart.description = description
        pieChart.centerText = centerText // "Spending"
        pieChart.setCenterTextSize(centerTextSize)
        pieChart.legend.isEnabled = isLegendEnabled
        pieChart.setEntryLabelColor(entryLabelColor)
        pieChart.setEntryLabelTextSize(entryLabelTextSize)
        pieChart.bind(pieData) { data = it; invalidate() }
    }
}