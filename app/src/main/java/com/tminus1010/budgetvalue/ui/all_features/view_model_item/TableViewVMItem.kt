package com.tminus1010.budgetvalue.ui.all_features.view_model_item

import com.tminus1010.tmcommonkotlin.misc.tmTableView.IHasToViewItemRecipe
import com.tminus1010.tmcommonkotlin.misc.tmTableView.TMTableView3

data class TableViewVMItem(
    val recipeGrid: List<List<IHasToViewItemRecipe>>,
    val dividerMap: Map<Int, IHasToViewItemRecipe> = emptyMap(),
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