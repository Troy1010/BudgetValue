package com.example.budgetvalue.layer_ui.TMTableView2

import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.example.budgetvalue.layer_ui.TMTableView.IViewItemRecipe
import com.tminus1010.tmcommonkotlin.logz.logz
import kotlin.math.max

// assume recipe2d[12][6]
// assume recipe2d[y][x]
// assume recipe2d[j][i]
class RecipeGrid(
    private val recipes2d: List<List<IViewItemRecipe>>,
): List<List<IViewItemRecipe>> by recipes2d {
    private fun getColumnHeight(j: Int): Int {
        return recipes2d[j]
            .map { it.intrinsicHeight }
            .fold(0) { acc, v -> max(acc, v) }
    }
    private fun getRowWidth(i: Int): Int {
        return recipes2d
            .map { it[i].intrinsicWidth }
            .fold(0) { acc, v -> max(acc, v) }
    }
    fun createResizedView(i: Int, j: Int): View {
        logz("rrr ySize:${this.size} xSize:${this[0].size} xSizeIsSame:${this.map { it.size }.all { it == this[0].size }}")
        return recipes2d[j][i].createView().apply {
            if (layoutParams == null)
                layoutParams = ViewGroup.LayoutParams(
                    getRowWidth(i),
                    getColumnHeight(j)
                )
            else
                updateLayoutParams {
                    width = getRowWidth(i)
                    height = getColumnHeight(j)
                }
        }
    }
}