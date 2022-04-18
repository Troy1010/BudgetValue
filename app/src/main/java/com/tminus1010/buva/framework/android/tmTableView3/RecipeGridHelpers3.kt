package com.tminus1010.buva.framework.android.tmTableView3

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import kotlin.math.max


@Deprecated("use commonlib's TMTableView")
interface IColumnWidthsProvider3 {
    fun getColumnWidth(i: Int): Int
}
@Deprecated("use commonlib's TMTableView")
class ColWidthsProviderFixedWidth3(recipes2d: List<List<IViewItemRecipe3>>, fixedWidth: Int): IColumnWidthsProvider3 {
    private val colWidths = ColumnWidthCalculator3.generateColumnWidths(recipes2d, fixedWidth)
    override fun getColumnWidth(i: Int) = colWidths[i]
}
@Deprecated("use commonlib's TMTableView")
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
@Deprecated("use commonlib's TMTableView")
interface IRowHeightProvider3 {
    fun getRowHeight(j: Int): Int
}
@Deprecated("use commonlib's TMTableView")
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
            .fold(0) { acc, (i, v) -> max(acc, v.intrinsicHeight(columnHeightProvider.getColumnWidth(i))) }
            .also { rowHeights[j] = it }
    }
}