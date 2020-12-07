package com.example.budgetvalue.layer_ui.TMTableView2

import android.content.Context
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetvalue.R
import com.example.budgetvalue.intrinsicHeight2
import com.example.budgetvalue.intrinsicWidth2
import com.example.budgetvalue.layer_ui.TMTableView.Decoration
import com.example.budgetvalue.layer_ui.TMTableView.IViewItemRecipe
import com.example.budgetvalue.layer_ui.misc.GenericRecyclerViewAdapter5

fun createColumn(context: Context, columnViewItemRecipes: Iterable<IViewItemRecipe>): RecyclerView {
    return RecyclerView(context)
        .apply {
            adapter = InnerRecyclerViewAdapter(context, columnViewItemRecipes)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(Decoration(context, Decoration.VERTICAL))
        }
}

fun bindColumn(
    columnView: RecyclerView,
    columnViewItemRecipes: Iterable<IViewItemRecipe>,
) {
    columnView.layoutParams = RecyclerView.LayoutParams(columnView.intrinsicWidth2, RecyclerView.LayoutParams.MATCH_PARENT)
}