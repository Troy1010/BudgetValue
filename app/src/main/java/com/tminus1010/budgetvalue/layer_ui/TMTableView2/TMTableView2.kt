package com.tminus1010.budgetvalue.layer_ui.TMTableView2

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.tminus1010.budgetvalue.Orientation
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.layer_ui.TMTableView.IViewItemRecipe
import com.tminus1010.tmcommonkotlin.rx.extensions.children
import com.tminus1010.tmcommonkotlin.rx.extensions.clearItemDecorations
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.tableview_layout2.view.*
import java.util.concurrent.TimeUnit

class TMTableView2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    var tableView: View? = null
    var disposable: Disposable? = null

    fun initialize(
        recipeGrid: RecipeGrid,
        dividerMap: Map<Int, IViewItemRecipe> = emptyMap(),
        colFreezeCount: Int = 0,
        rowFreezeCount: Int = 0,
    ) {
        // * I was experiencing race conditions, so I am awaiting widthObservable before using the main thread to render views.
        widthObservable
            .take(1)
            .timeout(5, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                inflateAndBind(
                    recipeGrid,
                    dividerMap,
                    colFreezeCount,
                    rowFreezeCount,
                    SynchronizedScrollListener(Orientation.HORIZONTAL),
                )
            }
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
            recyclerview_columnheaders.clearItemDecorations()
            recyclerview_columnheaders.addItemDecoration(InnerFrozenRowDecoration(context, Orientation.HORIZONTAL, recipeGrid, rowFreezeCount))
            recyclerview_columnheaders.clearOnScrollListeners()
            recyclerview_columnheaders.addOnScrollListener(synchronizedScrollListener)
        }
        // # Cells
        recyclerview_tier1.adapter = OuterRVAdapter(context, recipeGrid, rowFreezeCount, synchronizedScrollListener)
        recyclerview_tier1.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        recyclerview_tier1.clearItemDecorations()
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

    init {
        // * A view needs to be inflated to trigger onSizeChanged
        View.inflate(context, R.layout.blank_view, this)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w != oldw) widthObservable.onNext(w)
    }

    val widthObservable = BehaviorSubject.create<Int>()
}

