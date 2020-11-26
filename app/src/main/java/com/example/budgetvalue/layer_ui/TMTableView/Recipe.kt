package com.example.budgetvalue.layer_ui.TMTableView

import android.view.View

/**
 * Recipe contains everything required by RecyclerViewRecipeAdapter
 */
// TODO("Rename this. 'Recipe' is not a very informant name.")
data class Recipe <V:View, D:Any>(
    override val viewProvider: () -> V,
    override val data: D,
    val bindAction_: (V, D) -> Unit
) : IRecipe {
    override val bindAction = bindAction_ as (View, Any) -> Unit
    override val intrinsicWidth : Int
        get() {
            val view = viewProvider()
            bindAction(view, data)
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            return view.measuredWidth
        }
}