package com.example.budgetvalue.layer_ui.TMTableView2

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetvalue.extensions.scrollTo
import com.example.budgetvalue.intrinsicWidth2
import com.example.budgetvalue.layer_ui.TMTableView.IViewItemRecipe
import com.example.budgetvalue.measureUnspecified
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject

class ViewItemRecipeRecyclerViewAdapter2(
    val context: Context,
    val viewItemRecipe2D: List<Iterable<IViewItemRecipe>>
) : RecyclerView.Adapter<ViewItemRecipeRecyclerViewAdapter2.ViewHolder>() {
    constructor(context: Context, viewItemRecipe2D: Iterable<Iterable<IViewItemRecipe>>)
            : this(context, viewItemRecipe2D.toList())
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    //
    override fun onCreateViewHolder(parent: ViewGroup, yPos: Int): ViewHolder {
        return ViewHolder(createInnerRV(context, viewItemRecipe2D[yPos]))
    }
    override fun getItemViewType(position: Int) = position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        bindInnerRV((holder.itemView as RecyclerView), viewItemRecipe2D[holder.adapterPosition])
    }
    override fun getItemCount() = viewItemRecipe2D.size
    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        // # Synchronize vertical scroll initialization
        ignoreScroll = true
        ((holder.itemView as RecyclerView).layoutManager as LinearLayoutManager).scrollTo(scrollPosObservable.value)
        holder.itemView.measureUnspecified()
        ignoreScroll = false
    }
}