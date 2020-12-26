package com.example.budgetvalue.layer_ui.TMTableView2

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.example.budgetvalue.Orientation
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
        inflateAndBind(
            RecipeGrid(recipes2D_.map { it.toList() }),
            dividerMap,
            colFreezeCount,
            rowFreezeCount,
            SynchronizedScrollListener(Orientation.HORIZONTAL),
        )
    }

    private fun inflateAndBind(
        recipeGrid: RecipeGrid,
        dividerMap: Map<Int, IViewItemRecipe>,
        colFreezeCount: Int,
        rowFreezeCount: Int,
        synchronizedScrollListener: SynchronizedScrollListener,
    ) {
        // # Inflate tableView
        if (tableView == null) tableView = View.inflate(context, R.layout.tableview_layout2, this)
        // # Freeze rows
        if (rowFreezeCount>1) TODO()
        if (rowFreezeCount==1) {
            recyclerview_columnheaders.adapter = InnerRVAdapter(context, recipeGrid, 0)
            recyclerview_columnheaders.layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
            recyclerview_columnheaders.addItemDecoration(InnerFrozenRowDecoration(context, Orientation.HORIZONTAL, recipeGrid, rowFreezeCount))
            recyclerview_columnheaders.addOnScrollListener(synchronizedScrollListener)
        }
        // # Cells
        recyclerview_tier1.adapter = OuterRVAdapter(context, recipeGrid, rowFreezeCount, synchronizedScrollListener)
        recyclerview_tier1.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        recyclerview_tier1.addItemDecoration(OuterDecoration(context, Orientation.VERTICAL, dividerMap, recipeGrid, colFreezeCount, rowFreezeCount))
        // ## Synchronize scrolling
        disposable?.dispose()
        disposable = synchronizedScrollListener.scrollObservable
            .subscribe { (v, dx) ->
                // ### Scroll children in recyclerview_tier1
                recyclerview_tier1.layoutManager!!.children
                    .filter { it != v }
                    .forEach {
                        (it as? RecyclerView)
                            ?.also { synchronizedScrollListener.ignoredScrollBy(it, dx, 0) }
                    }
                // ### Scroll frozen row
                if (recyclerview_columnheaders != v) {
                    synchronizedScrollListener.ignoredScrollBy(recyclerview_columnheaders, dx, 0)
                }
            }
    }
}

