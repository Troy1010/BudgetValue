package com.example.budgetvalue.layer_ui.TMTableView2

import android.content.Context
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.example.budgetvalue.R
import com.example.budgetvalue.layer_ui.TMTableView.IViewItemRecipe

fun createColumn(context: Context, columnViewItemRecipes: Iterable<IViewItemRecipe>): LinearLayout {
    val view = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL }
    columnViewItemRecipes.forEach { view.addView(it.viewProvider()) }
    view.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
    view.dividerPadding = 1
    view.dividerDrawable = ContextCompat.getDrawable(context, R.drawable.divider)!!
    return view
}

fun bindColumn(
    columnView: LinearLayout,
    columnViewItemRecipes: Iterable<IViewItemRecipe>,
) {
    columnViewItemRecipes
        .withIndex()
        .forEach { (yPos, cellData) ->
            cellData.bindAction(columnView[yPos], cellData.data)
        }
}