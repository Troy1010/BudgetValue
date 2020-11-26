package com.example.budgetvalue.layer_ui.TMTableView

import android.content.Context
import android.view.View
import android.widget.TextView
import com.example.budgetvalue.R

class ViewItemRecipeFactory<V : View, D : Any>(
    val viewFactory: () -> V,
    val bindAction: (V, D) -> Unit
) {
    fun createMany(datas: Iterable<D>): Iterable<ViewItemRecipe<V, D>> {
        return datas.map { ViewItemRecipe(viewFactory, it, bindAction) }
    }
    fun createOne(data: D): Iterable<ViewItemRecipe<V, D>> {
        return listOf(ViewItemRecipe(viewFactory, data, bindAction))
    }

    companion object {
        // These are just some common ViewItemRecipeFactory that you might want.
        fun createHeaderRecipeFactory(context: Context): ViewItemRecipeFactory<TextView, String> {
            return ViewItemRecipeFactory(
                { View.inflate(context, R.layout.tableview_header, null) as TextView },
                { view: TextView, s: String? -> view.text = s }
            )
        }
        fun createCellRecipeFactory(context: Context): ViewItemRecipeFactory<TextView, String> {
            return ViewItemRecipeFactory(
                { View.inflate(context, R.layout.tableview_text_view, null) as TextView },
                { view: TextView, s: String? -> view.text = s }
            )
        }
    }
}