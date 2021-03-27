package com.tminus1010.budgetvalue.aa_core.middleware.ui.tmTableView2

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.budgetvalue.aa_core.middleware.Orientation
import com.tminus1010.budgetvalue.aa_core.middleware.ui.tmTableView.Decoration
import com.tminus1010.budgetvalue.aa_core.middleware.measureUnspecified
import com.tminus1010.tmcommonkotlin.misc.extensions.scrollTo

class OuterRVAdapter(
    val context: Context,
    val recipeGrid: RecipeGrid,
    val rowFreezeCount: Int,
    val synchronizedScrollListener: SynchronizedScrollListener
) : RecyclerView.Adapter<OuterRVAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    //
    override fun onCreateViewHolder(parent: ViewGroup, j: Int) = ViewHolder(createInnerRV(j))
    override fun getItemViewType(position: Int) = position + rowFreezeCount
    override fun onBindViewHolder(holder: ViewHolder, position: Int) { }
    override fun getItemCount() = recipeGrid.size - rowFreezeCount
    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        // # Synchronize scroll initialization
        ((holder.itemView as RecyclerView).layoutManager as LinearLayoutManager).scrollTo(synchronizedScrollListener.scrollPosObservable.value)
        holder.itemView.measureUnspecified() // Why is this necessary? (otherwise, there will be extra inner scroll space after outer scrolling)
    }
    fun createInnerRV(j: Int): RecyclerView {
        return RecyclerView(context)
            .apply {
                adapter = InnerRVAdapter(context, recipeGrid, j)
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                addItemDecoration(Decoration(context, Orientation.HORIZONTAL))
                addOnScrollListener(synchronizedScrollListener)
                layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, recipeGrid.getRowHeight(j))
            }
    }
}