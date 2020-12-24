package com.example.budgetvalue.layer_ui.TMTableView2

import com.example.budgetvalue.layer_ui.TMTableView.IViewItemRecipe
import kotlin.math.max

// assume recipe2d[y][x]
// assume recipe2d[j][i]
class RecipeGrid(
    private val recipes2d: List<List<IViewItemRecipe>>,
): List<List<IViewItemRecipe>> by recipes2d {
    fun getColumnHeight(j: Int): Int {
        return recipes2d[j]
            .map { it.intrinsicHeight }
            .fold(0) { acc, v -> max(acc, v) }
    }
    fun getRowWidth(i: Int): Int {
        return recipes2d
            .map { it[i].intrinsicWidth }
            .fold(0) { acc, v -> max(acc, v) }
    }
}