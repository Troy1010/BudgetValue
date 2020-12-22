package com.example.budgetvalue.layer_ui.TMTableView2

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.example.budgetvalue.R
import com.example.budgetvalue.extensions.children
import com.example.budgetvalue.layer_ui.TMTableView.Decoration
import com.example.budgetvalue.layer_ui.TMTableView.IViewItemRecipe
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.android.synthetic.main.tableview_layout.view.*

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
        freezeCountVert: Int = 0,
        freezeCountHorz: Int = 0,
    ) {
        recipe2D.onNext(recipes2D_) // TODO("Very hacky")
        inflateAndBind(recipes2D_, dividerMap, freezeCountVert, freezeCountHorz)
    }

    private fun inflateAndBind(
        viewItemRecipe2D: Iterable<Iterable<IViewItemRecipe>>,
        separatorMap: Map<Int, IViewItemRecipe>,
        freezeCountVert: Int,
        freezeCountHorz: Int,
    ) {
        val viewItemRecipe2DRedefined = viewItemRecipe2D.map { it.toList() }.toList()
        // # Inflate tableView
        if (tableView == null) tableView = View.inflate(context, R.layout.tableview_layout, this)
        // # Freeze columns/rows
        // TODO("Frozen columns/rows")
        // # Cells
        recyclerview_tier1.adapter = ViewItemRecipeRecyclerViewAdapter2(context, viewItemRecipe2D)
        recyclerview_tier1.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        recyclerview_tier1.addItemDecoration(TableViewDecorationTier1(context, Decoration.VERTICAL, separatorMap, viewItemRecipe2DRedefined, freezeCountHorz))
        // ## Synchronize scrolling
        disposable?.dispose()
        disposable = scrollObservable
            .subscribe { (v, dx) ->
                this.recyclerview_tier1.layoutManager!!.children
                    .filter { it != v }
                    .forEach {
                        ignoreScroll = true
                        (it as? RecyclerView)?.scrollBy(dx, 0)
                        ignoreScroll = false
                    }
            }
    }
}

