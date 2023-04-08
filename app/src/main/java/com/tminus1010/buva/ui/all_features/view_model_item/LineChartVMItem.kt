package com.tminus1010.buva.ui.all_features.view_model_item

import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.tminus1010.buva.all_layers.extensions.toEntries
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import kotlinx.coroutines.flow.Flow

class LineChartVMItem(
    private val valuesAndLabels: Flow<List<Pair<Float, String>>>,
    private val description: Description? = null,
    private val isLegendEnabled: Boolean = false,
    private val textColor: Int = Color.WHITE,
    private val xAxisPosition: XAxis.XAxisPosition = XAxis.XAxisPosition.BOTTOM,
) {
    fun bind(lineChart: LineChart) {
        lineChart.xAxis.position = xAxisPosition
        lineChart.xAxis.textColor = textColor
        lineChart.axisLeft.textColor = textColor
        lineChart.axisRight.textColor = textColor
        lineChart.description = description
        lineChart.legend.isEnabled = isLegendEnabled
        lineChart.bind(valuesAndLabels) {
            data =
                LineData(LineDataSet(
                    it.map { it.first }.toEntries(),
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