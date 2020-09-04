package com.example.budgetvalue.layers.z_ui.views.TMTableView

import android.view.View
import android.widget.LinearLayout
import androidx.core.view.get
import com.example.budgetvalue.util.arrayListOfZeros
import com.example.budgetvalue.util.intrinsicWidth2
import com.example.tmcommonkotlin.logz
import java.lang.Math.max
import kotlin.math.ceil

object ColumnWidthCalculator {

    fun generateIntrinsicWidths(
        rowFactory: () -> LinearLayout,
        cellBindAction: (View, Any) -> Unit,
        data: List<String>,
        columnCount: Int
    ): List<Int> {
        val intrinsicWidths = ArrayList<Int>()
        val view = rowFactory()
        for ((i, x) in data.withIndex()) {
            val viewChild = view[i % columnCount]
            cellBindAction(viewChild, x)
            intrinsicWidths.add(
                viewChild.intrinsicWidth2
            )
        }
        return intrinsicWidths
    }

    fun generateMinWidths(
        cellFactory: () -> View,
        cellBindAction: (View, Any) -> Unit,
        data: List<String>
    ): List<Int> {
        val minWidths = ArrayList<Int>()
        for (s in data) {
            val view = cellFactory()
            cellBindAction(view, s)
            minWidths.add(view.intrinsicWidth2)
        }
        return minWidths
    }

    fun generateColumnWidths(
        minWidths: List<Int>,
        intrinsicWidths: List<Int>,
        parentWidth: Int
    ): List<Int> {
        val columnCount = minWidths.size
        //trigger: data set changed. input: data, layout. output: views will be correct size
        // define column widths
        val columnWidths = arrayListOfZeros(columnCount)
        for ((i, intrinsicWidth) in intrinsicWidths.withIndex()) {
            columnWidths[i % columnCount] =
                columnWidths[i % columnCount].coerceAtLeast(intrinsicWidth)
        }
        while (columnWidths.sum() < parentWidth) {
            for (i in columnWidths.indices) {
                columnWidths[i] = columnWidths[i] + 1
            }
        }
        if (columnWidths.sum() > parentWidth) {
            val ratio = parentWidth.toDouble() / columnWidths.sum().toDouble()
            for (i in columnWidths.indices) {
                columnWidths[i] = max(minWidths[i], ceil(columnWidths[i] * ratio).toInt() + 10)
            }
        }
        var loopCount = 0
        var i = 0
        while ((columnWidths.sum() > parentWidth) && (loopCount<10000)) {
            i = (i+1)%columnCount
            columnWidths[i] = max(minWidths[i], columnWidths[i] - 1)
            loopCount++
        }
        return columnWidths
    }
}