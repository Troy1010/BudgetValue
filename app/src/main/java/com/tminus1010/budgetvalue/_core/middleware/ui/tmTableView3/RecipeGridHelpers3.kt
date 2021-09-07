package com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import kotlin.math.max


interface IColumnWidthsProvider3 {
    fun getColumnWidth(i: Int): Int
}
class ColWidthsProviderFixedWidth3(recipes2d: List<List<IViewItemRecipe3>>, fixedWidth: Int): IColumnWidthsProvider3 {
    private val colWidths = ColumnWidthCalculator3.generateColumnWidths(recipes2d, fixedWidth)
    override fun getColumnWidth(i: Int) = colWidths[i]
}
class ColWidthsProvider3(val recipes2d: List<List<IViewItemRecipe3>>): IColumnWidthsProvider3 {
    private val colWidths = HashMap<Int, Int>()
    init {
        Completable.fromCallable {
            recipes2d.getOrNull(0)?.indices?.forEach { getColumnWidth(it) }
        }.subscribeOn(AndroidSchedulers.mainThread()).subscribe()
    }
    override fun getColumnWidth(i: Int): Int {
        return colWidths[i] ?: recipes2d
            .fold(0) { acc, v -> max(acc, v[i].intrinsicWidth) }
            .also { colWidths[i] = it }
    }
}
interface IRowHeightProvider3 {
    fun getRowHeight(j: Int): Int
}
class RowHeightProvider3(val recipes2d: List<List<IViewItemRecipe3>>, val columnHeightProvider: IColumnWidthsProvider3): IRowHeightProvider3 {
    private val rowHeights = HashMap<Int, Int>()
    init {
        Completable.fromCallable {
            recipes2d.indices.forEach { getRowHeight(it) }
        }.subscribeOn(AndroidSchedulers.mainThread()).subscribe()
    }
    override fun getRowHeight(j: Int): Int {
        return rowHeights[j] ?: recipes2d[j]
            .withIndex()
            .fold(0) { acc, (i, v) -> max(acc, v.intrinsicHeight2(columnHeightProvider.getColumnWidth(i))) }
            .also { rowHeights[j] = it }
    }
}