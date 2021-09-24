package com.tminus1010.budgetvalue.all.presentation_and_view.services

import android.graphics.Color
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.tminus1010.budgetvalue._core.models.CategoryAmounts
import kotlin.math.absoluteValue

fun createPieData(categoryAmounts: CategoryAmounts): PieData {
    val entries = categoryAmounts.map { PieEntry(it.value.toFloat().absoluteValue) }
    val dataSet = PieDataSet(entries, "Election Results")
    dataSet.setDrawIcons(false)
    dataSet.sliceSpace = 3f
    dataSet.iconsOffset = MPPointF(0F, 40F)
    dataSet.selectionShift = 5f

    // add a lot of colors
    val colors = ArrayList<Int>()
    for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
    for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
    for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
    for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
    for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
    colors.add(ColorTemplate.getHoloBlue())
    dataSet.colors = colors
//        dataSet.selectionShift = 0f
    return PieData(dataSet).apply {
        setValueFormatter(PercentFormatter())
        setValueTextSize(11f)
        setValueTextColor(Color.WHITE)
    }
}