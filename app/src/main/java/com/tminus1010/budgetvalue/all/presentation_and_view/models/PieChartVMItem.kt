package com.tminus1010.budgetvalue.all.presentation_and_view.models

import android.graphics.Color
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.tminus1010.budgetvalue._core.all.extensions.bind
import io.reactivex.rxjava3.core.Observable

class PieChartVMItem(
    private val pieData: Observable<PieData>,
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