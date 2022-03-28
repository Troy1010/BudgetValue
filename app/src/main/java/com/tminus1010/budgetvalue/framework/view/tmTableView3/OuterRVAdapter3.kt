package com.tminus1010.budgetvalue.framework.view.tmTableView3

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.budgetvalue.framework.view.Orientation
import com.tminus1010.budgetvalue.framework.view.tmTableView.Decoration
import com.tminus1010.budgetvalue.framework.view.tmTableView2.SynchronizedScrollListener
import com.tminus1010.tmcommonkotlin.misc.extensions.measureUnspecified
import com.tminus1010.tmcommonkotlin.misc.extensions.scrollTo

@Deprecated("use commonlib's TMTableView")
class OuterRVAdapter3(
    val context: Context,
    val recipeGrid: RecipeGrid3,
    val rowFreezeCount: Int,
    val synchronizedScrollListener: SynchronizedScrollListener,
    val noDividers: Boolean
) : RecyclerView.Adapter<OuterRVAdapter3.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, j: Int) = RecyclerView(context)
        .apply {
            adapter = InnerRVAdapter(recipeGrid, j)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            if (!noDividers)
                addItemDecoration(Decoration(context, Orientation.HORIZONTAL))
            addOnScrollListener(synchronizedScrollListener)
            layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, recipeGrid.getRowHeight(j))
        }
        .let { ViewHolder(it) }

    override fun getItemViewType(position: Int) = position + rowFreezeCount
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {}
    override fun getItemCount() = recipeGrid.size - rowFreezeCount
    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        // # Synchronize scroll initialization
        ((holder.itemView as RecyclerView).layoutManager as LinearLayoutManager).scrollTo(synchronizedScrollListener.scrollPosObservable.value)
        holder.itemView.measureUnspecified() // Why is this necessary? (otherwise, there will be extra inner scroll space after outer scrolling)
    }
}