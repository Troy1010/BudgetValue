package com.example.budgetvalue.layer_ui.TMTableView2

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.example.budgetvalue.R
import com.example.budgetvalue.extensions.children
import com.example.budgetvalue.layer_ui.TMTableView.Decoration
import com.example.budgetvalue.layer_ui.TMTableView.IViewItemRecipe
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.android.synthetic.main.tableview_layout2.view.*

class TMTableView2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    var tableView: View? = null
    var disposable: Disposable? = null

    fun initialize(
        recipes2D_: Iterable<Iterable<IViewItemRecipe>>,
        dividerMap: Map<Int, IViewItemRecipe> = emptyMap(),
        colFreezeCount: Int = 0,
        rowFreezeCount: Int = 0,
    ) {
        recipe2D.onNext(recipes2D_) // TODO("Very hacky")
        inflateAndBind(RecipeGrid(recipes2D_.map { it.toList() }), dividerMap, colFreezeCount, rowFreezeCount)
    }

    private fun inflateAndBind(
        recipeGrid: RecipeGrid,
        dividerMap: Map<Int, IViewItemRecipe>,
        colFreezeCount: Int,
        rowFreezeCount: Int,
    ) {
        // # Inflate tableView
        if (tableView == null) tableView = View.inflate(context, R.layout.tableview_layout2, this)
        // # Freeze rows
        if (rowFreezeCount>1) TODO()
        if (rowFreezeCount==1) {
            recyclerview_columnheaders.adapter = InnerRecyclerViewAdapter(context, recipeGrid, 0)
            recyclerview_columnheaders.layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
            recyclerview_columnheaders.addItemDecoration(FrozenRowDecoration(context, HORIZONTAL, recipeGrid, rowFreezeCount))
            recyclerview_columnheaders.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (!ignoreScroll)
                        scrollObservable.onNext(Pair(recyclerView, dx))
                    super.onScrolled(recyclerView, dx, dy)
                }
            })
        }
        // # Cells
        recyclerview_tier1.adapter = ViewItemRecipeRecyclerViewAdapter2(context, recipeGrid)
        recyclerview_tier1.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        recyclerview_tier1.addItemDecoration(TableViewDecorationTier1(context, Decoration.VERTICAL, dividerMap, recipeGrid, colFreezeCount))
        // ## Synchronize scrolling
        disposable?.dispose()
        disposable = scrollObservable
            .subscribe { (v, dx) ->
                // scroll children in recyclerview_tier1
                recyclerview_tier1.layoutManager!!.children
                    .filter { it != v }
                    .forEach {
                        ignoreScroll = true
                        (it as? RecyclerView)?.scrollBy(dx, 0)
                        ignoreScroll = false
                    }
                // scroll frozen row
                if (recyclerview_columnheaders != v) {
                    ignoreScroll = true
                    recyclerview_columnheaders.scrollBy(dx, 0)
                    ignoreScroll = false
                }
            }
    }
}

