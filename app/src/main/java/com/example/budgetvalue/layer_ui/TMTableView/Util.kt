package com.example.budgetvalue.layer_ui.TMTableView

import android.content.Context
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.example.budgetvalue.R
import io.reactivex.rxjava3.subjects.BehaviorSubject


fun createRow(context: Context, rowRecipes: List<ICellRecipe>): LinearLayout {
    val view = LinearLayout(context)
    for (cellRecipe in rowRecipes) {
        val cellView = cellRecipe.viewFactory()
        view.addView(cellView)
    }
    view.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
    view.dividerPadding = 1
    view.dividerDrawable = ContextCompat.getDrawable(context, R.drawable.divider)!!
    return view
}

fun bindRow(
    rowView: LinearLayout,
    rowRecipes: List<ICellRecipe>,
    columnWidthsObservable: BehaviorSubject<List<Int>>
) {
    columnWidthsObservable.filter { it.isNotEmpty() }.take(1).subscribe {
        for ((xPos, cellData) in rowRecipes.withIndex()) {
            cellData.bindAction(rowView[xPos], cellData.data)
            rowView[xPos].layoutParams = LinearLayout.LayoutParams(
                it[xPos],
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }
    }
}