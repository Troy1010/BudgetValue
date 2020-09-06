package com.example.budgetvalue.layers.z_ui.TMTableView

import android.view.View

data class TableViewColumnData<V: View, D:Any, V2, D2> (
    val header: D,
    val headerViewFactory: ()->V,
    val headerBindAction: (V, D) -> Unit,
    val data: List<D2>,
    val cellViewFactory: ()->V2,
    val cellBindAction: (V2, D2) -> Unit
)