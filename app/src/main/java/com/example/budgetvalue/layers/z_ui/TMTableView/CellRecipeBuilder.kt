package com.example.budgetvalue.layers.z_ui.TMTableView

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.view.setPadding
import com.example.budgetvalue.R

class CellRecipeBuilder<V : View, D : Any>(
    val viewFactory: () -> V,
    val bindAction: (V, D) -> Unit
) {
    fun build(datas: List<D>): List<CellRecipe<V, D>> {
        return datas.map { CellRecipe(viewFactory, it, bindAction) }
    }
    fun buildOne(data: D) = CellRecipe(viewFactory, data, bindAction)

    companion object {
        operator fun invoke(context: Context) = this(context, Default.CELL)
        operator fun invoke(context: Context, e: Default): CellRecipeBuilder<TextView, String> {
            return when (e) {
                Default.HEADER -> CellRecipeBuilder(
                    { View.inflate(context, R.layout.tableview_header, null) as TextView },
                    { view: TextView, s: String? -> view.text = s }
                )
                else -> CellRecipeBuilder(
                    {
                        TextView(context)
                            .apply {
                                setTextColor(Color.WHITE)
                                setPadding(10)
                                gravity = Gravity.CENTER
                            }
                    },
                    { view: TextView, s: String? -> view.text = s }
                )
            }
        }
    }
    enum class Default {
        CELL, HEADER
    }
}