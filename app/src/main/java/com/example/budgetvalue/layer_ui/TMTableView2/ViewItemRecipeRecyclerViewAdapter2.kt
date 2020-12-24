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
import com.tminus1010.tmcommonkotlin.logz.logz

class ViewItemRecipeRecyclerViewAdapter2(
    val context: Context,
    val viewItemRecipe2D: RecipeGrid
) : RecyclerView.Adapter<ViewItemRecipeRecyclerViewAdapter2.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    //
    override fun onCreateViewHolder(parent: ViewGroup, j: Int): ViewHolder {
        return ViewHolder(createInnerRV(j))
    }
    override fun getItemViewType(position: Int) = position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) { }
    override fun getItemCount() = viewItemRecipe2D.size.also { logz("viewItemRecipe2D.size:${viewItemRecipe2D.size}") }
    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        // # Synchronize vertical scroll initialization
        ignoreScroll = true
        ((holder.itemView as RecyclerView).layoutManager as LinearLayoutManager).scrollTo(scrollPosObservable.value)
        holder.itemView.measureUnspecified()
        ignoreScroll = false
    }
    fun createInnerRV(j: Int): RecyclerView {
        return RecyclerView(context)
            .apply {
                adapter = InnerRecyclerViewAdapter(context, viewItemRecipe2D, j)
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                addItemDecoration(Decoration(context, Decoration.HORIZONTAL))
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        if (!ignoreScroll)
                            scrollObservable.onNext(Pair(recyclerView, dx))
                        super.onScrolled(recyclerView, dx, dy)
                    }
                })
                layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, intrinsicHeight2)
            }
    }
}