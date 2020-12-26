package com.example.budgetvalue.layer_ui.TMTableView2

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetvalue.extensions.scrollTo
import com.example.budgetvalue.intrinsicHeight2
import com.example.budgetvalue.layer_ui.TMTableView.Decoration
import com.example.budgetvalue.measureUnspecified

class RecipeGridOuterRVAdapter(
    val context: Context,
    val viewItemRecipe2D: RecipeGrid,
    val rowFreezeCount: Int,
    val myScrollListener: MyScrollListener
) : RecyclerView.Adapter<RecipeGridOuterRVAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    //
    override fun onCreateViewHolder(parent: ViewGroup, j: Int): ViewHolder {
        return ViewHolder(createInnerRV(j))
    }
    override fun getItemViewType(position: Int) = position + rowFreezeCount
    override fun onBindViewHolder(holder: ViewHolder, position: Int) { }
    override fun getItemCount() = viewItemRecipe2D.size - rowFreezeCount
    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        // # Synchronize scroll initialization
        ((holder.itemView as RecyclerView).layoutManager as LinearLayoutManager).scrollTo(myScrollListener.scrollPosObservable.value)
        holder.itemView.measureUnspecified()
    }
    fun createInnerRV(j: Int): RecyclerView {
        return RecyclerView(context)
            .apply {
                adapter = RecipeGridInnerRVAdapter(context, viewItemRecipe2D, j)
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                addItemDecoration(Decoration(context, Decoration.HORIZONTAL))
                addOnScrollListener(myScrollListener)
                layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, intrinsicHeight2)
            }
    }
}