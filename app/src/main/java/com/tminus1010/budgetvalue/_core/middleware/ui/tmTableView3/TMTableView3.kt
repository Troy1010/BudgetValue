package com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.findViewTreeLifecycleOwner
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
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit

class TMTableView3 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    private var disposable: Disposable? = null
    private var fitItemWidthsInsideTableDisposable: Disposable? = null
    private var isWrapContentVertically = false

    init {
        isWrapContentVertically = attrs?.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height")
            ?.toIntOrNull() == WRAP_CONTENT
    }

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
        if (recipeGrid.isEmpty()) error("recipeGrid was empty. This error is thrown b/c without it, an empty recipeGrid causes a no-stacetrace crash after 6s.")
        if (shouldFitItemWidthsInsideTable)
            widthObservable()
                .take(1)
                .timeout(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .observe(findViewTreeLifecycleOwner()!!) { width ->
                    inflateAndBind(
                        RecipeGrid3(recipeGrid, width),
                        dividerMap,
                        colFreezeCount,
                        rowFreezeCount,
                        SynchronizedScrollListener(Orientation.HORIZONTAL),
                    )
                }
                .also { fitItemWidthsInsideTableDisposable?.dispose(); fitItemWidthsInsideTableDisposable = it }
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
        val vb = TableviewBinding.inflate(LayoutInflater.from(context), this, true)
        // # Freeze rows
        if (rowFreezeCount > 1) TODO()
        if (rowFreezeCount == 1) {
            vb.recyclerviewColumnheaders.adapter = InnerRVAdapter(recipeGrid, 0)
            vb.recyclerviewColumnheaders.layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
            vb.recyclerviewColumnheaders.clearItemDecorations()
            vb.recyclerviewColumnheaders.addItemDecoration(InnerFrozenRowDecoration3(context, Orientation.HORIZONTAL, recipeGrid, rowFreezeCount))
            vb.recyclerviewColumnheaders.clearOnScrollListeners()
            vb.recyclerviewColumnheaders.addOnScrollListener(synchronizedScrollListener)
        }
        // # Cells
        if (isWrapContentVertically) vb.recyclerviewTier1.updateLayoutParams { height = WRAP_CONTENT } // passing wrap_content to children
        vb.recyclerviewTier1.adapter = OuterRVAdapter3(context, recipeGrid, rowFreezeCount, synchronizedScrollListener)
        vb.recyclerviewTier1.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        vb.recyclerviewTier1.clearItemDecorations()
        vb.recyclerviewTier1.addItemDecoration(OuterDecoration3(context, Orientation.VERTICAL, dividerMap, recipeGrid, colFreezeCount, rowFreezeCount))
        // ## Synchronize scrolling
        disposable?.dispose()
        disposable = synchronizedScrollListener.scrollObservable
            .observe(findViewTreeLifecycleOwner()!!) { (v, dx) ->
                // ### Scroll children in recyclerview_tier1
                vb.recyclerviewTier1.layoutManager!!.children
                    .filter { it != v }
                    .forEach {
                        (it as? RecyclerView)
                            ?.also { synchronizedScrollListener.ignoredScrollBy(it, dx, 0) }
                    }
                // ### Scroll frozen row
                if (vb.recyclerviewColumnheaders != v) {
                    synchronizedScrollListener.ignoredScrollBy(vb.recyclerviewColumnheaders, dx, 0)
                }
            }
    }
}

