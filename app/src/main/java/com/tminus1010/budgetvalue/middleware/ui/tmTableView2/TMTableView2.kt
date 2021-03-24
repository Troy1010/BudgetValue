package com.tminus1010.budgetvalue.middleware.ui.tmTableView2

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.tminus1010.budgetvalue.middleware.Orientation
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.databinding.TableviewLayout2Binding
import com.tminus1010.budgetvalue.middleware.ui.tmTableView.IViewItemRecipe
import com.tminus1010.tmcommonkotlin.misc.extensions.children
import com.tminus1010.tmcommonkotlin.misc.extensions.clearItemDecorations
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class TMTableView2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    var disposable: Disposable? = null

    /**
     * @param shouldFitItemWidthsInsideTable
     * If set to true, items will be resized to make the entire grid fit within the width of the table.
     */
    fun initialize(
        recipeGrid: List<List<IViewItemRecipe>>,
        shouldFitItemWidthsInsideTable: Boolean = false,
        dividerMap: Map<Int, IViewItemRecipe> = emptyMap(),
        colFreezeCount: Int = 0,
        rowFreezeCount: Int = 0,
    ) {
        if (shouldFitItemWidthsInsideTable)
            widthObservable
                .take(1)
                .timeout(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { width ->
                    inflateAndBind(
                        RecipeGrid(recipeGrid, width),
                        dividerMap,
                        colFreezeCount,
                        rowFreezeCount,
                        SynchronizedScrollListener(Orientation.HORIZONTAL),
                    )
                }
        else
            inflateAndBind(
                RecipeGrid(recipeGrid),
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
        removeAllViews()
        val binding = TableviewLayout2Binding.inflate(LayoutInflater.from(context), this, true)
        // # Freeze rows
        if (rowFreezeCount>1) TODO()
        if (rowFreezeCount==1) {
            binding.recyclerviewColumnheaders.adapter = InnerRVAdapter(context, recipeGrid, 0)
            binding.recyclerviewColumnheaders.layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
            binding.recyclerviewColumnheaders.clearItemDecorations()
            binding.recyclerviewColumnheaders.addItemDecoration(InnerFrozenRowDecoration(context, Orientation.HORIZONTAL, recipeGrid, rowFreezeCount))
            binding.recyclerviewColumnheaders.clearOnScrollListeners()
            binding.recyclerviewColumnheaders.addOnScrollListener(synchronizedScrollListener)
        }
        // # Cells
        binding.recyclerviewTier1.adapter = OuterRVAdapter(context, recipeGrid, rowFreezeCount, synchronizedScrollListener)
        binding.recyclerviewTier1.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        binding.recyclerviewTier1.clearItemDecorations()
        binding.recyclerviewTier1.addItemDecoration(OuterDecoration(context, Orientation.VERTICAL, dividerMap, recipeGrid, colFreezeCount, rowFreezeCount))
        // ## Synchronize scrolling
        disposable?.dispose()
        disposable = synchronizedScrollListener.scrollObservable
            .subscribe { (v, dx) ->
                // ### Scroll children in recyclerview_tier1
                binding.recyclerviewTier1.layoutManager!!.children
                    .filter { it != v }
                    .forEach {
                        (it as? RecyclerView)
                            ?.also { synchronizedScrollListener.ignoredScrollBy(it, dx, 0) }
                    }
                // ### Scroll frozen row
                if (binding.recyclerviewColumnheaders != v) {
                    synchronizedScrollListener.ignoredScrollBy(binding.recyclerviewColumnheaders, dx, 0)
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

