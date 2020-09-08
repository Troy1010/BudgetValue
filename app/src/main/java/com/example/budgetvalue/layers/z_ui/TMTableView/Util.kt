package com.example.budgetvalue.layers.z_ui.TMTableView

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.get
import com.example.tmcommonkotlin.logz
import io.reactivex.rxjava3.subjects.BehaviorSubject


fun createRow(context: Context, rowRecipes: List<ICellRecipe>): LinearLayout {
    val view = LinearLayout(context)
    for (cellRecipe in rowRecipes) {
        val cellView = cellRecipe.viewFactory()
        view.addView(cellView)
    }
    return view
}

fun bindRow(
    rowView: LinearLayout,
    rowRecipes: List<ICellRecipe>,
    columnWidthsObservable: BehaviorSubject<List<Int>>
) {
    for ((xPos, cellData) in rowRecipes.withIndex()) {
        cellData.bindAction(rowView[xPos], cellData.data)
        rowView[xPos].layoutParams = LinearLayout.LayoutParams(
            columnWidthsObservable.value.getOrNull(xPos) ?: 0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
    }
}