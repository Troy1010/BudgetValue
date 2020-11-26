package com.example.budgetvalue.layer_ui.TMTableView

import android.content.Context
import android.view.View
import android.widget.TextView
import com.example.budgetvalue.R

class RecipeFactory<V : View, D : Any>(
    val viewFactory: () -> V,
    val bindAction: (V, D) -> Unit
) {
    fun createMany(datas: Iterable<D>): Iterable<Recipe<V, D>> {
        return datas.map { Recipe(viewFactory, it, bindAction) }
    }
    fun createOne(data: D): Iterable<Recipe<V, D>> {
        return listOf(Recipe(viewFactory, data, bindAction))
    }

    companion object {
        operator fun invoke(context: Context, defaultType: DefaultType): RecipeFactory<TextView, String> {
            return when (defaultType) {
                DefaultType.HEADER -> RecipeFactory(
                    { View.inflate(context, R.layout.tableview_header, null) as TextView },
                    { view: TextView, s: String? -> view.text = s }
                )
                else -> RecipeFactory(
                    { View.inflate(context, R.layout.tableview_text_view, null) as TextView },
                    { view: TextView, s: String? -> view.text = s }
                )
            }
        }
    }
    enum class DefaultType { CELL, HEADER }
}