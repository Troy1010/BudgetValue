package com.tminus1010.budgetvalue.framework.android.tmTableView3

import android.content.Context
import androidx.viewbinding.ViewBinding
import com.tminus1010.budgetvalue.databinding.ItemEmptyBinding
import com.tminus1010.tmcommonkotlin.misc.extensions.easySetHeight
import com.tminus1010.tmcommonkotlin.misc.extensions.easySetWidth
import com.tminus1010.tmcommonkotlin.misc.extensions.pairwise

/**
 * This class keeps data that depends on the entire grid, such as rowHeight and columnWidth
 * For now, the class assumptions are:
 *      assume heights and widths do not change
 *      assume recipe2d[y][x]
 *      assume recipe2d[j][i]
 *
 * @param fixedWidth a value besides null will resize items to make the entire grid have fixedWidth.
 */
@Deprecated("use commonlib's TMTableView")
class RecipeGrid3(
    private val recipes2d: List<List<IViewItemRecipe3>>,
    private val fixedWidth: Int? = null,
) : List<List<IViewItemRecipe3>> by recipes2d,
    IRowHeightProvider3,
    IColumnWidthsProvider3 by if (fixedWidth == null) ColWidthsProvider3(recipes2d) else ColWidthsProviderFixedWidth3(recipes2d, fixedWidth) {

    fun createResizedView(i: Int, j: Int): ViewBinding {
        return recipes2d[j][i].createVB()
            .apply { root.easySetWidth(getColumnWidth(i)) }
            .apply { root.easySetHeight(getRowHeight(j)) }
    }

    companion object {
        fun fillToEnsure2dGrid(context: Context, recipes2d: List<List<IViewItemRecipe3>>): List<List<IViewItemRecipe3>> {
            val maxSize = recipes2d.fold(0) { acc, v -> acc.coerceAtLeast(v.size) }
            return recipes2d.map {
                val amountOfEmptyRecipeItemsToAdd = maxSize - it.size
                it.plus((0 until amountOfEmptyRecipeItemsToAdd).map { ViewItemRecipe3__(context, ItemEmptyBinding::inflate) { } })
            }
        }

        fun assert2dGrid(recipes2d: List<List<IViewItemRecipe3>>) {
            // # Assert that all inner lists have equal size
            recipes2d.pairwise().forEach { if (it.first.size != it.second.size) error("All sub-lists must be equal size.") }
        }
    }

    val rowHeightProvider = RowHeightProvider3(recipes2d, this)
    override fun getRowHeight(j: Int): Int = rowHeightProvider.getRowHeight(j)
}