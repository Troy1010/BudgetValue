package com.example.budgetvalue.layer_ui.TMTableView

import android.content.Context
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.example.budgetvalue.R


fun createRow(context: Context, rowViewItemRecipes: List<IViewItemRecipe>): LinearLayout {
    val view = LinearLayout(context)
    for (cellRecipe in rowViewItemRecipes) {
        val cellView = cellRecipe.viewProvider()
        view.addView(cellView)
    }
    view.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
    view.dividerPadding = 1
    view.dividerDrawable = ContextCompat.getDrawable(context, R.drawable.divider)!!
    return view
}

fun bindRow(
    rowView: LinearLayout,
    rowViewItemRecipes: List<IViewItemRecipe>,
    columnWidths: List<Int>
) {
    for ((xPos, cellData) in rowViewItemRecipes.withIndex()) {
        cellData.bindAction(rowView[xPos], cellData.data)
        rowView[xPos].layoutParams = LinearLayout.LayoutParams(
            columnWidths[xPos],
            LinearLayout.LayoutParams.MATCH_PARENT
        )
    }

}