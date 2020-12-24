package com.example.budgetvalue.layer_ui.TMTableView2

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.tmcommonkotlin.logz.logz

class InnerRecyclerViewAdapter(
    val context: Context,
    val recipeGrid: RecipeGrid,
    val j: Int
) : RecyclerView.Adapter<InnerRecyclerViewAdapter.ViewHolder>() {
    init {
        logz("recipeGrid xSize:${recipeGrid.size} ySize:${recipeGrid[0].size} ySizeIsSame:${recipeGrid.map { it.size }.all { it == recipeGrid[0].size }}")
    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder {
        logz("i:$i j:$j")
        return ViewHolder(recipeGrid.createResizedView(i, j))
    }

    override fun getItemViewType(position: Int) = position

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        recipeGrid[j][holder.adapterPosition].bindView(holder.itemView)
    }

    override fun getItemCount() = recipeGrid[0].size.also { logz("recipeGrid[0].size:${it}") }
//    override fun onViewAttachedToWindow(holder: ViewHolder) {
//        super.onViewAttachedToWindow(holder)
//        recipe2D!!.value
//            .map { try { it.toList()[holder.adapterPosition].intrinsicWidth } catch (e:Exception) { 0 } }
//            .fold(0) { acc, v -> max(acc, v) }
//            .also { holder.itemView.updateLayoutParams { width = it } }
//    }
}