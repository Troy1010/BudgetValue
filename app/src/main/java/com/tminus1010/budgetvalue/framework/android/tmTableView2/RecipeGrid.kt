package com.tminus1010.budgetvalue.framework.android.tmTableView2

import android.view.View
import com.tminus1010.budgetvalue.framework.android.tmTableView.IViewItemRecipe
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
class RecipeGrid(
    private val recipes2d: List<List<IViewItemRecipe>>,
    private val fixedWidth: Int? = null,
) : List<List<IViewItemRecipe>> by recipes2d,
    IRowHeightProvider by RowHeightProvider(recipes2d),
    IColumnWidthsProvider by if (fixedWidth==null) ColWidthsProvider(recipes2d) else ColWidthsProviderFixedWidth(recipes2d, fixedWidth) {
    init {
        // # Assert that all inner lists have equal size
        recipes2d.pairwise().forEach { if (it.first.size != it.second.size) error("All sub-lists must be equal size.") }
    }

    fun createResizedView(i: Int, j: Int): View {
        return recipes2d[j][i].createView()
            .apply { easySetWidth(getColumnWidth(i)) }
            .apply { easySetHeight(getRowHeight(j)) }
    }
}