package com.tminus1010.buva.ui.all_features.view_model_item

import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.tminus1010.buva.all_layers.extensions.withIndex
import com.tminus1010.tmcommonkotlin.core.extensions.associate
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import kotlinx.coroutines.flow.Flow

class LineChartVMItem(
    private val mapLabelToValues: Flow<Map<String, List<Pair<Int, Float>>>>,
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
        lineChart.bind(mapLabelToValues) { mapLabelToValues ->
            data =
                LineData(
                    (0..(mapLabelToValues.values.maxByOrNull { it.count() }?.count()?.minus(1) ?: 0)).map { j ->
                        LineDataSet(
                            mapLabelToValues.withIndex().associate { it.key.index to it.value.getOrNull(j) }.mapNotNull { (i, v) ->
                                v?.let { Entry(i.toFloat(), v.second) }
                            },
                            "label$j",
                        ).apply {
                            valueTextColor = textColor
                            color = mapLabelToValues.values.maxByOrNull { it.count() }?.get(j)?.first ?: Color.WHITE
                        }
                    }
                )
            xAxis.valueFormatter =
                object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        // TODO: This might not work correctly. Sometimes it falls back to null, which I don't think is expected.
                        return mapLabelToValues.keys.withIndex().find { it.index.toFloat() == value }?.value ?: ""
                    }
                }
            invalidate()
        }
    }
}