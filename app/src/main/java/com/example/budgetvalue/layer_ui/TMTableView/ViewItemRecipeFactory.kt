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
        operator fun invoke(context: Context, defaultType: DefaultType): ViewItemRecipeFactory<TextView, String> {
            return when (defaultType) {
                DefaultType.HEADER -> ViewItemRecipeFactory(
                    { View.inflate(context, R.layout.tableview_header, null) as TextView },
                    { view: TextView, s: String? -> view.text = s }
                )
                else -> ViewItemRecipeFactory(
                    { View.inflate(context, R.layout.tableview_text_view, null) as TextView },
                    { view: TextView, s: String? -> view.text = s }
                )
            }
        }
    }
    enum class DefaultType { CELL, HEADER }
}