package com.tminus1010.budgetvalue.layer_ui.TMTableView

import android.view.View
import com.tminus1010.budgetvalue.measureUnspecified

data class ViewItemRecipe<V : View, D : Any>(
    private val viewProvider: () -> V,
    private val data: D,
    private val bindAction: (V, D) -> Unit,
) : IViewItemRecipe {
    // This cast allows IViewItemRecipe of different view types to be stored together
    @Suppress("UNCHECKED_CAST")
    private val bindAction_ = bindAction as (View, Any) -> Unit
    override val intrinsicWidth
        get() = createBoundView().apply { measureUnspecified() }.measuredWidth
    override val intrinsicHeight
        get() = createBoundView().apply { measureUnspecified() }.measuredHeight
    override fun createView(): View {
        return viewProvider()
    }

    override fun createBoundView(): View {
        return createView().also { bindAction_(it, data) }
    }

    override fun bindView(view: View) {
        bindAction_(view, data)
    }
}