package com.tminus1010.budgetvalue.layer_ui.TMTableView2

import com.tminus1010.budgetvalue.layer_ui.TMTableView.ColumnWidthCalculator
import com.tminus1010.budgetvalue.layer_ui.TMTableView.IViewItemRecipe
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlin.math.max


interface IColumnWidthsProvider {
    fun getColumnWidth(i: Int): Int
}
class ColWidthsProviderFixedWidth(recipes2d: List<List<IViewItemRecipe>>, fixedWidth: Int): IColumnWidthsProvider {
    private val colWidths = ColumnWidthCalculator.generateColumnWidths(recipes2d, fixedWidth)
    override fun getColumnWidth(i: Int) = colWidths[i]
}
class ColWidthsProvider(val recipes2d: List<List<IViewItemRecipe>>): IColumnWidthsProvider {
    private val colWidths = HashMap<Int, Int>()
    init {
        Completable.fromCallable {
            recipes2d[0].indices.forEach { getColumnWidth(it) }
        }.subscribeOn(Schedulers.computation()).subscribe()
    }
    override fun getColumnWidth(i: Int): Int {
        return colWidths[i] ?: recipes2d
            .fold(0) { acc, v -> max(acc, v[i].intrinsicWidth) }
            .also { colWidths[i] = it }
    }
}
interface IRowHeightProvider {
    fun getRowHeight(j: Int): Int
}
class RowHeightProvider(val recipes2d: List<List<IViewItemRecipe>>): IRowHeightProvider {
    private val rowHeights = HashMap<Int, Int>()
    init {
        Completable.fromCallable {
            recipes2d.indices.forEach { getRowHeight(it) }
        }.subscribeOn(Schedulers.computation()).subscribe()
    }
    override fun getRowHeight(j: Int): Int {
        return rowHeights[j] ?: recipes2d[j]
            .fold(0) { acc, v -> max(acc, v.intrinsicHeight) }
            .also { rowHeights[j] = it }
    }
}