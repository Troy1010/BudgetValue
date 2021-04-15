package com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3

import android.view.View
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
class RecipeGrid3(
    private val recipes2d: List<List<IViewItemRecipe3>>,
    private val fixedWidth: Int? = null,
) : List<List<IViewItemRecipe3>> by recipes2d,
    IRowHeightProvider3 by RowHeightProvider3(recipes2d),
    IColumnWidthsProvider3 by if (fixedWidth==null) ColWidthsProvider3(recipes2d) else ColWidthsProviderFixedWidth3(recipes2d, fixedWidth) {
    init {
        // # Assert that all inner lists have equal size
        recipes2d.pairwise().forEach { if (it.first.size != it.second.size) error("All sub-lists must be equal size.") }
    }

    fun createResizedView(i: Int, j: Int): View {
        return recipes2d[j][i].createVB().root
            .apply { easySetWidth(getColumnWidth(i)) }
            .apply { easySetHeight(getRowHeight(j)) }
    }
}