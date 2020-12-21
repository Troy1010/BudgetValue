package com.example.budgetvalue.layer_ui.TMTableView

import android.view.View

data class ViewItemRecipe<V : View, D : Any>(
    override val viewProvider: () -> V,
    override val data: D,
    val bindAction_: (V, D) -> Unit,
) : IViewItemRecipe {
    override val bindAction = bindAction_ as (View, Any) -> Unit
    override val intrinsicWidth: Int
        get() {
            val view = viewProvider()
            bindAction(view, data)
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            return view.measuredWidth
        }
    override val intrinsicHeight: Int
        get() {
            val view = viewProvider()
            bindAction(view, data)
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            return view.measuredHeight
        }

    override fun createBoundView(): View {
        return viewProvider().also { bindAction(it, data) }
    }
}