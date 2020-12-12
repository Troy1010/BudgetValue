package com.example.budgetvalue.layer_ui.TMTableView2

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetvalue.R
import com.example.budgetvalue.extensions.children
import com.example.budgetvalue.intrinsicHeight2
import com.example.budgetvalue.layer_ui.TMTableView.Decoration
import com.example.budgetvalue.layer_ui.TMTableView.IViewItemRecipe
import com.tminus1010.tmcommonkotlin.logz.logz
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.tableview_layout.view.*
import java.lang.Math.max

class TMTableView2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    val TAG = TMTableView2::class.java.simpleName

    var tableView: View? = null
    val _recipe2D = BehaviorSubject.create<Iterable<Iterable<IViewItemRecipe>>>()

    var disposable: Disposable? = null

    init {
        _recipe2D
            .subscribe { inflateAndBind(it) }
    }

    fun setRecipes(viewItemRecipe2D: Iterable<Iterable<IViewItemRecipe>>) {
        _recipe2D.onNext(viewItemRecipe2D)
    }

    fun inflateAndBind(viewItemRecipe2D: Iterable<Iterable<IViewItemRecipe>>) {
        // # Inflate tableView
        if (tableView == null) tableView = View.inflate(context, R.layout.tableview_layout, this)
        // # Freeze columns/rows
        // TODO("Frozen columns/rows")
        // # Cells
        recyclerview_tier1.adapter = ViewItemRecipeRecyclerViewAdapter2(
            context,
            viewItemRecipe2D
        )
        val addViewObservable = BehaviorSubject.create<Unit>()
        recyclerview_tier1.layoutManager = object : LinearLayoutManager(context, HORIZONTAL, false) {
            override fun addView(child: View?) {
                super.addView(child)
                addViewObservable.onNext(Unit)
                // # Set yScroll
                // TODO("This needs to be observed somewhere else so that it is fired when a view becomes visible")
                ignoreVertScroll = true
                (child as? RecyclerView)?.scrollBy(0, yScrollPosObservable.value)
                ignoreVertScroll = false
            }
        }
        recyclerview_tier1.addItemDecoration(Decoration(context, Decoration.HORIZONTAL))
        // ## Synchronize visible heights
        fun syncHeights() {
            val heightBarrier = recyclerview_tier1.layoutManager!!.children
                .fold(0) { acc, v -> max(acc, v.intrinsicHeight2) }
            logz("heightBarrier:$heightBarrier")
            recyclerview_tier1.layoutManager!!.children
                .forEach {
                    logz("height:${it.height}")
                    logz("layoutParamsHeight:${it.layoutParams.height}")
//                    if (it.height != heightBarrier)
//                        it.updateLayoutParams { height = heightBarrier }
//                    it.requestLayout()
                }
        }
        addViewObservable.subscribe { logz("addViewObservable") ; syncHeights() }
        // ## Synchronize vertical scrolling
        disposable?.dispose()
        disposable = vertScrollObservable
            .subscribe { (v, dy) ->
                this.recyclerview_tier1.layoutManager!!.children
                    .filter { it != v }
                    .forEach {
                        ignoreVertScroll = true
                        (it as? RecyclerView)?.scrollBy(0, dy)
                        ignoreVertScroll = false
                    }
            }
    }
}

