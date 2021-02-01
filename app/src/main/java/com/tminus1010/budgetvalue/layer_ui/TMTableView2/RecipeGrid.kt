package com.tminus1010.budgetvalue.layer_ui.TMTableView2

import android.view.View
import com.tminus1010.budgetvalue.extensions.easySetHeight
import com.tminus1010.budgetvalue.extensions.easySetWidth
import com.tminus1010.budgetvalue.layer_ui.TMTableView.ColumnWidthCalculator
import com.tminus1010.budgetvalue.layer_ui.TMTableView.IViewItemRecipe
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.ReplaySubject
import java.util.concurrent.TimeUnit
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

    val fixedWidthRedefined = fixedWidth?.toBehaviorSubject()

    private val colWidths: Single<List<Observable<Int>>> =
        Single.just(Unit)
            .observeOn(Schedulers.newThread())
            .compose { upstream ->
                if (fixedWidthRedefined==null)
                    upstream.map { recipes2d[0].indices.map { calcColumnWidthWithoutFixedWidth(it).let { Observable.just(it) } } }
                else {
                    Single.create<List<Observable<Int>>> { downstream ->
                        val list = mutableListOf<ReplaySubject<Int>>()
                            .apply { repeat(recipes2d[0].size) { add(ReplaySubject.create()) } }
                        fixedWidthRedefined
                            .map { ColumnWidthCalculator.generateColumnWidths(recipes2d, it) }
                            .subscribe { it.withIndex().forEach { (i, v) -> list[i].onNext(v) } } // TODO("Disposable. If it continues from downstream, a race condition happens")
                        downstream.onSuccess(list)
                    }
                        .subscribeOn(Schedulers.computation())
                }
            }
            .timeout(5, TimeUnit.SECONDS)
            .cache()
    private val rowHeights = HashMap<Int, Int>()

    init {
        // # Calculate height and widths
        Completable.fromCallable {
            for (j in recipes2d.indices) getRowHeight(j)
            if (fixedWidth==null)
                for (i in recipes2d[0].indices) getColumnWidth(i)
        }.subscribeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    fun getRowHeight(j: Int): Int {
        return rowHeights[j] ?: recipes2d[j]
            .map { it.intrinsicHeight }
            .fold(0) { acc, v -> max(acc, v) }
            .also { rowHeights[j] = it }
    }

    fun getColumnWidth(i: Int): Observable<Int> {
        return colWidths.observeOn(Schedulers.newThread()).flatMapObservable { it[i] }.timeout(5, TimeUnit.SECONDS)
    }

    private fun calcColumnWidthWithoutFixedWidth(i: Int): Int {
        return recipes2d
            .map { it[i].intrinsicWidth }
            .fold(0) { acc, v -> max(acc, v) }
    }


    fun createResizedView(i: Int, j: Int): View {
        return recipes2d[j][i].createView()
            .apply { easySetWidth(getColumnWidth(i).blockingFirst()) }
            .apply { easySetHeight(getRowHeight(j)) }
    }
}