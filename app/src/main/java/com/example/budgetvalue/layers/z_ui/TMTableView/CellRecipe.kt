package com.example.budgetvalue.layers.z_ui.TMTableView

import android.view.View

data class CellRecipe <V:View, D:Any>(
    override val viewFactory: () -> V,
    val bindAction_: (V, D) -> Unit,
    override val data: D
) : ICellData {
    override val bindAction = bindAction_ as (View, Any) -> Unit
    override val intrinsicWidth : Int
        get() {
            val view = viewFactory()
            bindAction(view, data)
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            return view.measuredWidth
        }
}