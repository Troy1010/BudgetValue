package com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.tminus1010.budgetvalue._core.extensions.widthObservable
import com.tminus1010.budgetvalue._core.middleware.Orientation
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView2.SynchronizedScrollListener
import com.tminus1010.budgetvalue.databinding.TableviewBinding
import com.tminus1010.tmcommonkotlin.misc.extensions.children
import com.tminus1010.tmcommonkotlin.misc.extensions.clearItemDecorations
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit

class TMTableView3 @JvmOverloads constructor(
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
        recipeGrid: List<List<IViewItemRecipe3>>,
        shouldFitItemWidthsInsideTable: Boolean = false,
        dividerMap: Map<Int, IViewItemRecipe3> = emptyMap(),
        colFreezeCount: Int = 0,
        rowFreezeCount: Int = 0,
    ) {
        if (shouldFitItemWidthsInsideTable)
            widthObservable()
                .take(1)
                .timeout(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { width ->
                    inflateAndBind(
                        RecipeGrid3(recipeGrid, width),
                        dividerMap,
                        colFreezeCount,
                        rowFreezeCount,
                        SynchronizedScrollListener(Orientation.HORIZONTAL),
                    )
                }
        else
            inflateAndBind(
                RecipeGrid3(recipeGrid),
                dividerMap,
                colFreezeCount,
                rowFreezeCount,
                SynchronizedScrollListener(Orientation.HORIZONTAL),
            )
    }

    private fun inflateAndBind(
        recipeGrid: RecipeGrid3,
        dividerMap: Map<Int, IViewItemRecipe3>,
        colFreezeCount: Int,
        rowFreezeCount: Int,
        synchronizedScrollListener: SynchronizedScrollListener,
    ) {
        removeAllViews()
        val binding = TableviewBinding.inflate(LayoutInflater.from(context), this, true)
        // # Freeze rows
        if (rowFreezeCount>1) TODO()
        if (rowFreezeCount==1) {
            binding.recyclerviewColumnheaders.adapter = InnerRVAdapter(recipeGrid, 0)
            binding.recyclerviewColumnheaders.layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
            binding.recyclerviewColumnheaders.clearItemDecorations()
            binding.recyclerviewColumnheaders.addItemDecoration(InnerFrozenRowDecoration3(context, Orientation.HORIZONTAL, recipeGrid, rowFreezeCount))
            binding.recyclerviewColumnheaders.clearOnScrollListeners()
            binding.recyclerviewColumnheaders.addOnScrollListener(synchronizedScrollListener)
        }
        // # Cells
        binding.recyclerviewTier1.adapter = OuterRVAdapter3(context, recipeGrid, rowFreezeCount, synchronizedScrollListener)
        binding.recyclerviewTier1.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        binding.recyclerviewTier1.clearItemDecorations()
        binding.recyclerviewTier1.addItemDecoration(OuterDecoration3(context, Orientation.VERTICAL, dividerMap, recipeGrid, colFreezeCount, rowFreezeCount))
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
}
