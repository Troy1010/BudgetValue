package com.example.budgetvalue.layer_ui.TMTableView

import android.view.View

interface ICellRecipe {
    val viewProvider: () -> View
    val bindAction: (View, Any) -> Unit
    val data: Any
    val intrinsicWidth : Int
}