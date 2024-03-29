package com.tminus1010.buva.ui.all_features.view_model_item

import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipeFactory
import com.tminus1010.tmcommonkotlin.misc.tmTableView.TMTableView3

data class TableViewVMItem(
    val recipeGrid: List<List<ViewItemRecipeFactory>>,
    val dividerMap: Map<Int, ViewItemRecipeFactory> = emptyMap(),
    val shouldFitItemWidthsInsideTable: Boolean = false,
    val colFreezeCount: Int = 0,
    val rowFreezeCount: Int = 0,
    val noDividers: Boolean = false,
) {
    fun bind(tmTableView3: TMTableView3) {
        tmTableView3.initialize(
            recipeGrid = recipeGrid.map { it.map { it.toViewItemRecipe(tmTableView3.context) } },
            dividerMap = dividerMap.mapValues { it.value.toViewItemRecipe(tmTableView3.context) },
            shouldFitItemWidthsInsideTable = shouldFitItemWidthsInsideTable,
            colFreezeCount = colFreezeCount,
            rowFreezeCount = rowFreezeCount,
            noDividers = noDividers,
        )
    }
}