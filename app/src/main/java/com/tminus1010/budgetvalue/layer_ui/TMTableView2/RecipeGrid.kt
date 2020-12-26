package com.tminus1010.budgetvalue.layer_ui.TMTableView2

import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.tminus1010.budgetvalue.layer_ui.TMTableView.IViewItemRecipe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
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

    init {
        // # Calculate height and widths
        Observable.just(Unit)
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .subscribe {
                for (j in recipes2d.indices) getRowHeight(j)
                for (i in recipes2d[0].indices) getColumnWidth(i)
            }
    }

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
        return recipes2d[j][i].createView()
            .also {
                if (it.layoutParams == null)
                    it.layoutParams = ViewGroup.LayoutParams(
                        getColumnWidth(i),
                        getRowHeight(j)
                    )
                else
                    it.updateLayoutParams {
                        width = getColumnWidth(i)
                        height = getRowHeight(j)
                    }
            }
    }
}