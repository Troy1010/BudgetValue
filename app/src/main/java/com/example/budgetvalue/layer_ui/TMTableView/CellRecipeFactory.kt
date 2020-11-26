package com.example.budgetvalue.layer_ui.TMTableView

import android.content.Context
import android.view.View
import android.widget.TextView
import com.example.budgetvalue.R

class CellRecipeFactory<V : View, D : Any>(
    val viewFactory: () -> V,
    val bindAction: (V, D) -> Unit
) {
    fun createMany(datas: Iterable<D>): Iterable<CellRecipe<V, D>> {
        return datas.map { CellRecipe(viewFactory, it, bindAction) }
    }
    fun createOne(data: D): Iterable<CellRecipe<V, D>> {
        return listOf(CellRecipe(viewFactory, data, bindAction))
    }

    companion object {
        operator fun invoke(context: Context, defaultType: DefaultType): CellRecipeFactory<TextView, String> {
            return when (defaultType) {
                DefaultType.HEADER -> CellRecipeFactory(
                    { View.inflate(context, R.layout.tableview_header, null) as TextView },
                    { view: TextView, s: String? -> view.text = s }
                )
                else -> CellRecipeFactory(
                    { View.inflate(context, R.layout.tableview_text_view, null) as TextView },
                    { view: TextView, s: String? -> view.text = s }
                )
            }
        }
    }
    enum class DefaultType { CELL, HEADER }
}