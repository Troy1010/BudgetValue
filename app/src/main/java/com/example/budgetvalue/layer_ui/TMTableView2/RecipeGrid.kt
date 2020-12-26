package com.example.budgetvalue.layer_ui.TMTableView2

import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.example.budgetvalue.layer_ui.TMTableView.IViewItemRecipe
import kotlin.math.max

/**
 * This class keeps data that depends on the entire grid, such as rowHeight and columnWidth
 * For now, the class requires the assumptions:
 *      assume heights and widths do not change
 *      assume recipe2d[y][x]
 *      assume recipe2d[j][i]
 */
class RecipeGrid(
    private val recipes2d: List<List<IViewItemRecipe>>,
) : List<List<IViewItemRecipe>> by recipes2d {
    private val colHeights = HashMap<Int, Int>()
    private val rowHeights = HashMap<Int, Int>()
    fun getRowHeight(j: Int): Int {
        return rowHeights[j] ?: recipes2d[j]
            .map { it.intrinsicHeight }
            .fold(0) { acc, v -> max(acc, v) }
            .also { rowHeights[j] = it }
    }

    fun getColumnWidth(i: Int): Int {
        return colHeights[i] ?: recipes2d
            .map { it[i].intrinsicWidth }
            .fold(0) { acc, v -> max(acc, v) }
            .also { colHeights[i] = it }
    }

    fun createResizedView(i: Int, j: Int): View {
        return recipes2d[j][i].createView().apply {
            if (layoutParams == null)
                layoutParams = ViewGroup.LayoutParams(
                    getColumnWidth(i),
                    getRowHeight(j)
                )
            else
                updateLayoutParams {
                    width = getColumnWidth(i)
                    height = getRowHeight(j)
                }
        }
    }
}