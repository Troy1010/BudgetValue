package com.tminus1010.budgetvalue.layer_ui.TMTableView

import android.content.Context
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.tminus1010.budgetvalue.R


fun createRow(context: Context, rowViewItemRecipes: Iterable<IViewItemRecipe>): LinearLayout {
    val view = LinearLayout(context)
    rowViewItemRecipes.forEach { view.addView(it.createView()) }
    view.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
    view.dividerPadding = 1
    view.dividerDrawable = ContextCompat.getDrawable(context, R.drawable.divider)!!
    return view
}

fun bindRow(
    rowView: LinearLayout,
    rowViewItemRecipes: Iterable<IViewItemRecipe>,
    columnWidths: List<Int>
) {
    for ((xPos, recipe) in rowViewItemRecipes.withIndex()) {
        recipe.bindView(rowView[xPos])
        rowView[xPos].layoutParams = LinearLayout.LayoutParams(
            columnWidths[xPos],
            LinearLayout.LayoutParams.MATCH_PARENT
        )
    }

}