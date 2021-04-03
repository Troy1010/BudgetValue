package com.tminus1010.budgetvalue._core.middleware.ui.tmTableView

import com.tminus1010.budgetvalue._core.middleware.arrayListOfZeros
import com.tminus1010.tmcommonkotlin.misc.logz
import java.lang.Math.max
import kotlin.math.ceil

object ColumnWidthCalculator {
    fun generateIntrinsicWidths(viewItemRecipes: Iterable<Iterable<IViewItemRecipe>>): List<List<Int>> {
        val intrinsicWidths = ArrayList<ArrayList<Int>>()
        for ((yPos, rowData) in viewItemRecipes.withIndex()) {
            intrinsicWidths.add(ArrayList())
            for (cellData in rowData) {
                intrinsicWidths[yPos].add(cellData.intrinsicWidth)
            }
        }
        return intrinsicWidths
    }

    fun generateMinWidths(rowData: Iterable<IViewItemRecipe>) = rowData.map { it.intrinsicWidth }

    fun generateColumnWidths(
        viewItemRecipe2D: Iterable<Iterable<IViewItemRecipe>>,
        parentWidth: Int
    ): List<Int> {
        val minWidths = generateMinWidths(viewItemRecipe2D.first())
        val intrinsicWidths = generateIntrinsicWidths(viewItemRecipe2D)

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