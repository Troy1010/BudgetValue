package com.tminus1010.budgetvalue.layer_ui.TMTableView2

import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.tminus1010.budgetvalue.layer_ui.TMTableView.ColumnWidthCalculator
import com.tminus1010.budgetvalue.layer_ui.TMTableView.IViewItemRecipe
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlin.math.max

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
    private val fixedWidth: Observable<Int>? = null,
) : List<List<IViewItemRecipe>> by recipes2d {
    init {
        // # Assert that all inner lists have equal size
        recipes2d.fold(null) { acc: Int?, v ->
            v.size
                .also { if (acc != null && acc != it) error("All sub-lists must be equal size. acc:$acc") }
        }
    }
    
    private val colWidths = HashMap<Int, Int>()
    private val rowHeights = HashMap<Int, Int>()

    init {
        // # Calculate height and widths
        Completable.fromCallable {
            for (j in recipes2d.indices) getRowHeight(j)
            if (fixedWidth==null)
                for (i in recipes2d[0].indices) getColumnWidth(i)
            else
                fixedWidth.take(1).subscribe { // TODO("Handle take all")
                    colWidths.putAll(ColumnWidthCalculator.generateColumnWidths(recipes2d, it).withIndex().associate { it.index to it.value })
                }
        }.subscribeOn(Schedulers.computation()).subscribe()
    }

    fun getRowHeight(j: Int): Int {
        return rowHeights[j] ?: recipes2d[j]
            .map { it.intrinsicHeight }
            .fold(0) { acc, v -> max(acc, v) }
            .also { rowHeights[j] = it }
    }

    fun getColumnWidth(i: Int): Int {
        return if (fixedWidth==null) colWidths[i] ?: recipes2d
            .map { it[i].intrinsicWidth }
            .fold(0) { acc, v -> max(acc, v) }
            .also { colWidths[i] = it }
        else {
            while (colWidths[i] == null) Thread.sleep(10) // TODO("Handle fixedWidth take all")
            colWidths[i]!!
        }
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