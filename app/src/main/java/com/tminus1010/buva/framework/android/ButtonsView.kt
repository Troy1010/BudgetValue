package com.tminus1010.buva.framework.android

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tminus1010.buva.databinding.ItemButtonBinding
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.tmcommonkotlin.androidx.GenViewHolder

class ButtonsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.editTextStyle,
) : RecyclerView(context, attrs, defStyleAttr) {
    var buttons = emptyList<ButtonVMItem>()
        set(value) {
            field = value; adapter?.notifyDataSetChanged()
        }

    init {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        addItemDecoration(MarginDecoration(7))
        adapter = object : LifecycleRVAdapter2<GenViewHolder<ItemButtonBinding>>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                GenViewHolder(ItemButtonBinding.inflate(LayoutInflater.from(context), parent, false))

            override fun getItemCount() = buttons.size
            override fun onLifecycleAttached(holder: GenViewHolder<ItemButtonBinding>) {
                buttons[itemCount - 1 - holder.adapterPosition].bind(holder.vb.btnItem)
            }
        }
    }
}