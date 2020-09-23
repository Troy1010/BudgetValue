package com.example.budgetvalue.layer_ui.TMTableView

import android.content.Context
import android.view.View
import android.widget.TextView
import com.example.budgetvalue.R

class CellRecipeBuilder<V : View, D : Any>(
    val viewFactory: () -> V,
    val bindAction: (V, D) -> Unit
) {
    fun build(datas: List<D>): List<CellRecipe<V, D>> {
        return datas.map { CellRecipe(viewFactory, it, bindAction) }
    }
    fun buildOne(data: D): List<CellRecipe<V, D>> {
        return listOf(CellRecipe(viewFactory, data, bindAction))
    }
    fun buildOne2(data: D) = CellRecipe(viewFactory, data, bindAction)

    companion object {
        operator fun invoke(context: Context, e: Default): CellRecipeBuilder<TextView, String> {
            return when (e) {
                Default.HEADER -> CellRecipeBuilder(
                    { View.inflate(context, R.layout.tableview_header, null) as TextView },
                    { view: TextView, s: String? -> view.text = s }
                )
                else -> CellRecipeBuilder(
                    { View.inflate(context, R.layout.tableview_basic_cell, null) as TextView },
                    { view: TextView, s: String? -> view.text = s }
                )
            }
        }
    }
    enum class Default {
        CELL, HEADER
    }
}