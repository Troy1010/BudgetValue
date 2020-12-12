package com.example.budgetvalue.layer_ui.TMTableView2

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetvalue.intrinsicWidth2
import com.example.budgetvalue.layer_ui.TMTableView.Decoration
import com.example.budgetvalue.layer_ui.TMTableView.IViewItemRecipe
import io.reactivex.rxjava3.subjects.BehaviorSubject

val vertScrollObservable = BehaviorSubject.create<Pair<View, Int>>() // TODO("Hacky")
var ignoreVertScroll = false // TODO("Hacky")

fun createColumn(context: Context, columnViewItemRecipes: Iterable<IViewItemRecipe>): RecyclerView {
    return RecyclerView(context)
        .apply {
            adapter = InnerRecyclerViewAdapter(context, columnViewItemRecipes)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(Decoration(context, Decoration.VERTICAL))
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (!ignoreVertScroll)
                        vertScrollObservable.onNext(Pair(recyclerView, dy))
                    super.onScrolled(recyclerView, dx, dy)
                }
            })
        }
}

fun bindColumn2(
    columnView: RecyclerView,
    columnViewItemRecipes: Iterable<IViewItemRecipe>,
) {
    columnView.layoutParams = RecyclerView.LayoutParams(
        columnView.intrinsicWidth2,
        RecyclerView.LayoutParams.MATCH_PARENT
    )
}