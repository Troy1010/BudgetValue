package com.example.budgetvalue.layer_ui.TMTableView2

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetvalue.intrinsicHeight2
import com.example.budgetvalue.layer_ui.TMTableView.Decoration
import com.example.budgetvalue.layer_ui.TMTableView.IViewItemRecipe
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlin.math.max

val recipe2D = BehaviorSubject.create<Iterable<Iterable<IViewItemRecipe>>>()  // TODO("Hacky, only supports 1")
val firstColWidth = recipe2D
    .map {
        it
            .map { it.first().intrinsicWidth }
            .fold(0) { acc, v -> max(acc, v) }
    }
    .toBehaviorSubject()
val firstRowHeight = recipe2D
    .map {
        it
            .first()
            .map { it.intrinsicHeight }
            .fold(0) { acc, v -> max(acc, v) }
    }
    .toBehaviorSubject()
val scrollObservable = BehaviorSubject.create<Pair<View, Int>>() // TODO("Hacky")
var ignoreScroll = false // TODO("Hacky")
val scrollPosObservable = scrollObservable
    .map { it.second }
    .startWithItem(0)
    .scan(0) { acc, value -> acc + value }
    .toBehaviorSubject()

fun createInnerRV(context: Context, columnViewItemRecipes: Iterable<IViewItemRecipe>): RecyclerView {
    return RecyclerView(context)
        .apply {
            adapter = InnerRecyclerViewAdapter(context, columnViewItemRecipes)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(Decoration(context, Decoration.HORIZONTAL))
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (!ignoreScroll)
                        scrollObservable.onNext(Pair(recyclerView, dx))
                    super.onScrolled(recyclerView, dx, dy)
                }
            })
        }
}

fun bindInnerRV(
    columnView: RecyclerView,
    columnViewItemRecipes: Iterable<IViewItemRecipe>,
) {
    columnView.layoutParams = RecyclerView.LayoutParams(
        RecyclerView.LayoutParams.MATCH_PARENT,
        columnView.intrinsicHeight2
    )
}