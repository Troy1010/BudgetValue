package com.example.budgetvalue.layer_ui.TMTableView

import com.example.budgetvalue.arrayListOfZeros
import com.example.tmcommonkotlin.logz
import java.lang.Math.max
import kotlin.math.ceil

object ColumnWidthCalculator {
    fun generateIntrinsicWidths(cellRecipes: List<List<ICellRecipe>>): List<List<Int>> {
        val intrinsicWidths = ArrayList<ArrayList<Int>>()
        for ((yPos, rowData) in cellRecipes.withIndex()) {
            intrinsicWidths.add(ArrayList())
            for (cellData in rowData) {
                intrinsicWidths[yPos].add(cellData.intrinsicWidth)
            }
        }
        return intrinsicWidths
    }

    fun generateMinWidths(rowData: List<ICellRecipe>) = rowData.map { it.intrinsicWidth }

    fun generateColumnWidths(
        minWidths: List<Int>,
        intrinsicWidths: List<List<Int>>,
        parentWidth: Int
    ): List<Int> {
        if (minWidths.sum() > parentWidth)
            logz("WARNING`minWidths.sum():${minWidths.sum()} > parentWidth:$parentWidth")
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
        if (columnWidths.sum() != parentWidth)
            logz("WARNING`columnWidths.sum():${columnWidths.sum()} != parentWidth:$parentWidth")
        return columnWidths
    }
}