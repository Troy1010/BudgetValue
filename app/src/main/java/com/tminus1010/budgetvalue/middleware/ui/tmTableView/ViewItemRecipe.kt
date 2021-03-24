package com.tminus1010.budgetvalue.middleware.ui.tmTableView

import android.view.View
import com.tminus1010.budgetvalue.middleware.measureUnspecified
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable

data class ViewItemRecipe<V : View, D : Any?>(
    private val viewProvider: () -> V,
    private val data: D,
    private val bindAction: (V, D) -> Unit,
) : IViewItemRecipe {
    // This cast allows IViewItemRecipe of different view types to be stored together
    @Suppress("UNCHECKED_CAST")
    private val bindAction_ = bindAction as (View, Any?) -> Unit
    override val intrinsicWidth
        get() = createBoundView().apply { measureUnspecified() }.measuredWidth
    override val intrinsicHeight
        get() = createBoundView().apply { measureUnspecified() }.measuredHeight

    override fun createView(): View = viewProvider()
    override fun createBoundView(): View = createView().also { bindView(it) }

    override fun bindView(view: View) =
        try {
            bindAction_(view, data)
        } catch (e:android.util.AndroidRuntimeException) { // maybe mainThread is required
            Completable.fromAction { bindAction_(view, data) }
                .subscribeOn(AndroidSchedulers.mainThread())
                .blockingAwait()
        }
}