package com.example.budgetvalue.layer_ui.TMTableView2

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetvalue.intrinsicHeight2
import com.example.budgetvalue.intrinsicWidth2
import com.example.budgetvalue.layer_ui.TMTableView.IViewItemRecipe
import com.tminus1010.tmcommonkotlin.logz.logz
import com.tminus1010.tmcommonkotlin.misc.fnName

class InnerRecyclerViewAdapter(
    val context: Context,
    val viewItemRecipes: List<IViewItemRecipe>,
) : RecyclerView.Adapter<InnerRecyclerViewAdapter.ViewHolder>() {
    constructor(context: Context, viewItemRecipes: Iterable<IViewItemRecipe>)
            : this(context, viewItemRecipes.toList())
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(viewItemRecipes[viewType].viewProvider())
    }

    override fun getItemViewType(position: Int) = position

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = holder.itemView
        view.layoutParams = LinearLayout.LayoutParams(
            500,
            600
        )
        val bindAction = viewItemRecipes[holder.adapterPosition].bindAction
        val data = viewItemRecipes[holder.adapterPosition].data
        logz("$fnName. data:$data")
        bindAction(view, data)
    }

    override fun getItemCount(): Int {
        return viewItemRecipes.size
    }
}