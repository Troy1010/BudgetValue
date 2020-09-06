package com.example.budgetvalue.layers.z_ui.TMTableView

import android.view.View
import android.widget.LinearLayout
import androidx.core.view.get
import com.example.budgetvalue.util.arrayListOfZeros
import com.example.budgetvalue.util.intrinsicWidth2
import com.example.tmcommonkotlin.logz
import java.lang.Math.max
import kotlin.math.ceil

object ColumnWidthCalculator {
    fun generateIntrinsicWidths(cellDatas: List<List<TableViewCellData>>): List<List<Int>> {
        val intrinsicWidths = ArrayList<ArrayList<Int>>()
        for ((yPos, rowData) in cellDatas.withIndex()) {
            intrinsicWidths.add(ArrayList())
            for (cellData in rowData) {
                intrinsicWidths[yPos].add(cellData.intrinsicWidth)
            }
        }
        return intrinsicWidths
    }

    fun generateMinWidths(rowData: List<TableViewCellData>) = rowData.map { it.intrinsicWidth }

    fun generateColumnWidths(
        minWidths: List<Int>,
        intrinsicWidths: List<List<Int>>,
        parentWidth: Int
    ): List<Int> {
        val columnCount = minWidths.size
        // define column widths
        val columnWidths = arrayListOfZeros(columnCount)
        for ((yPos, rowData) in intrinsicWidths.withIndex()) {
            for (xPos in rowData.indices) {
                columnWidths[xPos] = max(columnWidths[xPos], intrinsicWidths[yPos][xPos])
            }
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