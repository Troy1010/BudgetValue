package com.example.budgetvalue.layers.z_ui.misc

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tmcommonkotlin.logz

open class GenericRecyclerViewAdapter5<V : View>(
    val itemFactory: () -> V,
    val bindDataAction: (RecyclerView.ViewHolder) -> Unit,
    val getItemCount_: () -> Int
) : RecyclerView.Adapter<GenericRecyclerViewAdapter5.ViewHolder>() {
    companion object {
        operator fun invoke(
            context: Context, itemLayout: Int,
            bindDataAction: (RecyclerView.ViewHolder) -> Unit,
            getItemCount_: () -> Int
        ): GenericRecyclerViewAdapter5<View> {
            return GenericRecyclerViewAdapter5(
                { LayoutInflater.from(context).inflate(itemLayout, null, false) },
                bindDataAction, getItemCount_
            )
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(itemFactory())
    }

    override fun getItemCount() = getItemCount_()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        bindDataAction(holder)
    }
}