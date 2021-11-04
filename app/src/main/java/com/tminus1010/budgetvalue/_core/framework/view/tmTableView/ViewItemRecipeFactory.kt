package com.tminus1010.budgetvalue._core.framework.view.tmTableView

import android.content.Context
import android.view.View
import android.widget.TextView
import com.tminus1010.budgetvalue.R

class ViewItemRecipeFactory<V : View, D : Any?>(
    val viewFactory: () -> V,
    val bindAction: (V, D) -> Unit,
) {
    fun createMany(datas: Iterable<D>) = datas.map { ViewItemRecipe(viewFactory, it, bindAction) }
    fun createOne2(data: D) = listOf(createOne(data))
    fun createOne(data: D) = ViewItemRecipe(viewFactory, data, bindAction)

    companion object {
        // These are just some common ViewItemRecipeFactory
        fun createHeaderRecipeFactory(context: Context): ViewItemRecipeFactory<TextView, String> {
            return ViewItemRecipeFactory(
                { View.inflate(context, R.layout.item_header, null) as TextView },
                { view: TextView, s: String? -> view.text = s }
            )
        }

        fun createCellRecipeFactory(context: Context): ViewItemRecipeFactory<TextView, String> {
            return ViewItemRecipeFactory(
                { View.inflate(context, R.layout.item_text_view, null) as TextView },
                { view: TextView, s: String? -> view.text = s }
            )
        }
    }
}