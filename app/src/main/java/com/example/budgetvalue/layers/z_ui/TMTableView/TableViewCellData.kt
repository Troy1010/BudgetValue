package com.example.budgetvalue.layers.z_ui.TMTableView

import android.view.View

data class TableViewCellData(
    val viewFactory: () -> View,
    val bindAction: (View, Any) -> Unit,
    val data: Any
) {
    val intrinsicWidth : Int
        get() {
            val view = viewFactory()
            bindAction(view, data)
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            return view.measuredWidth
        }
}