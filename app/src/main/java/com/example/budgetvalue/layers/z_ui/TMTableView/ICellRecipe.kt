package com.example.budgetvalue.layers.z_ui.TMTableView

import android.view.View

interface ICellRecipe {
    val viewFactory: () -> View
    val bindAction: (View, Any) -> Unit
    val data: Any
    val intrinsicWidth : Int
}