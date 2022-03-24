package com.tminus1010.budgetvalue.framework.view.tmTableView3

import java.lang.Math.max
import kotlin.math.floor

@Deprecated("use commonlib's TMTableView")
object ColumnWidthCalculator3 {
    fun generateIntrinsicWidths(viewItemRecipes: Iterable<Iterable<IViewItemRecipe3>>): List<List<Int>> {
        val intrinsicWidths = ArrayList<ArrayList<Int>>()
        for ((yPos, rowData) in viewItemRecipes.withIndex()) {
            intrinsicWidths.add(ArrayList())
            for (cellData in rowData) {
                intrinsicWidths[yPos].add(cellData.intrinsicWidth)
            }
        }
        return intrinsicWidths
    }

    fun generateMinWidths(rowData: Iterable<IViewItemRecipe3>) = rowData.map { it.intrinsicWidth }

    fun generateColumnWidths(
        viewItemRecipe2D: Iterable<Iterable<IViewItemRecipe3>>,
        parentWidth: Int,
    ): List<Int> {
        val minWidths = generateMinWidths(viewItemRecipe2D.first())
        val intrinsicWidths = generateIntrinsicWidths(viewItemRecipe2D)

        if (minWidths.sum() > parentWidth)
            logz("WARNING`minWidths.sum():${minWidths.sum()} > parentWidth:$parentWidth")
        val columnCount = minWidths.size
        // # fold 2d intrinsicWidths into 1d columnWidths by getting the max of each column.
        val columnWidths = (0 until columnCount).map { 0 }.toMutableList()
        for ((yPos, rowData) in intrinsicWidths.withIndex()) {
            for (xPos in rowData.indices) {
                columnWidths[xPos] = max(columnWidths[xPos], intrinsicWidths[yPos][xPos])
            }
        }
        // # If columnWidths is too big, find a strategy for making it smaller.
        if (columnWidths.sum() > parentWidth) {
            val widthToRemove = columnWidths.sum() - parentWidth
            // ## Find the biggest view that we can remove the entire widthToRemove from without taking over 50% or violating minWidths, if it exists.
            val indexedValueToRemoveFrom =
                columnWidths
                    .withIndex()
                    .filter { (i, x) -> widthToRemove < x / 2 && widthToRemove < x - minWidths[i] }
                    .maxByOrNull { (_, x) -> x }
            if (indexedValueToRemoveFrom != null) {
                columnWidths[indexedValueToRemoveFrom.index] = indexedValueToRemoveFrom.value - widthToRemove
            } else {
                // ## Remove from all columns evenly, rounding to prefer removing extra
                val ratio = parentWidth.toDouble() / columnWidths.sum().toDouble()
                for (i in columnWidths.indices) {
                    columnWidths[i] = max(minWidths[i], floor(columnWidths[i] * ratio).toInt() + 10)
                }
            }
        }
        // # If columnWidths is not big enough, make it bigger
        var i = 0
        while (columnWidths.sum() < parentWidth) {
            columnWidths[i] += 1
            i = (i + 1) % columnCount
        }
        //
        if (columnWidths.sum() != parentWidth)
            logz("WARNING`columnWidths.sum():${columnWidths.sum()} != parentWidth:$parentWidth")
        return columnWidths
    }
}