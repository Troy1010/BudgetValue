package com.example.budgetvalue.layers.z_ui.TMTableView

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

class TVVerticalRecyclerViewAdapter(val context: Context, val data2d:List<List<TableViewCellData>>, val columnWidthsObservable: BehaviorSubject<List<Int>>) : RecyclerView.Adapter<TVVerticalRecyclerViewAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    val viewHolders = ArrayList<ViewHolder>() // This is probably not recommended..

    override fun onCreateViewHolder(parent: ViewGroup, yPos: Int): ViewHolder {
        val recyclerViewT2 = RecyclerView(context)
        recyclerViewT2.layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        recyclerViewT2.adapter = TVHorizontalRecyclerViewAdapter(context, data2d[yPos], columnWidthsObservable)
        val viewHolder = ViewHolder(recyclerViewT2)
        viewHolders.add(viewHolder)
        return viewHolder
    }

    override fun getItemViewType(position: Int) = position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) { }
    override fun getItemCount() = data2d.size


    val streamDataChanged = PublishSubject.create<Unit>().also {
        this.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                it.onNext(Unit)
            }
        })
    }.also {
        it.subscribe {
            for (viewHolder in viewHolders) {
                (viewHolder.itemView as RecyclerView).adapter?.notifyDataSetChanged()
            }
        }
    }
}