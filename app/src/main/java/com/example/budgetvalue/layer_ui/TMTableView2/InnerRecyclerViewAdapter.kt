package com.example.budgetvalue.layer_ui.TMTableView2

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetvalue.layer_ui.TMTableView.IViewItemRecipe
import java.lang.Integer.max

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
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        val bindAction = viewItemRecipes[holder.adapterPosition].bindAction
        val data = viewItemRecipes[holder.adapterPosition].data
        bindAction(view, data)
    }

    override fun getItemCount() = viewItemRecipes.size
    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        recipe2D!!.value
            .map { try { it.toList()[holder.adapterPosition].intrinsicWidth } catch (e:Exception) { 0 } }
            .fold(0) { acc, v -> max(acc, v) }
            .also { holder.itemView.updateLayoutParams { width = it } }
    }
}