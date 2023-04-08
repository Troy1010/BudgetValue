package com.tminus1010.buva.ui.all_features.view_model_item

import android.graphics.Color
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.tminus1010.buva.all_layers.extensions.toBarEntries
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import kotlinx.coroutines.flow.Flow

class BarChartVMItem(
    private val valuesAndLabels: Flow<List<Pair<Float, String>>>,
    private val description: Description? = null,
    private val isLegendEnabled: Boolean = false,
    private val textColor: Int = Color.WHITE,
    private val xAxisPosition: XAxis.XAxisPosition = XAxis.XAxisPosition.BOTTOM,
) {
    fun bind(barChart: BarChart) {
        barChart.xAxis.position = xAxisPosition
        barChart.xAxis.textColor = textColor
        barChart.axisLeft.textColor = textColor
        barChart.axisRight.textColor = textColor
        barChart.description = description
        barChart.legend.isEnabled = isLegendEnabled
        barChart.bind(valuesAndLabels) {
            data =
                BarData(BarDataSet(
                    it.map { it.first }.toBarEntries(),
                    "label",
                ).apply { valueTextColor = textColor })
            xAxis.valueFormatter =
                object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        // TODO: There should be a more simple and performant way to do this.
                        return it.map { it.second }.withIndex().associate { it.index.toFloat() to it.value }[value]!!
                    }
                }
            invalidate()
        }
    }
}