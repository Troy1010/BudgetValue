package com.example.budgetvalue.layer_ui.TMTableView

import android.view.View
import com.example.budgetvalue.measureUnspecified

data class ViewItemRecipe<V : View, D : Any>(
    override val viewProvider: () -> V,
    override val data: D,
    val bindAction_: (V, D) -> Unit,
) : IViewItemRecipe {
    override val bindAction = bindAction_ as (View, Any) -> Unit
    override val intrinsicWidth by lazy { createBoundView().also { it.measureUnspecified() }.measuredWidth }
    override val intrinsicHeight by lazy { createBoundView().also { it.measureUnspecified() }.measuredHeight }

    override fun createBoundView(): View {
        return viewProvider().also { bindAction(it, data) }
    }
}