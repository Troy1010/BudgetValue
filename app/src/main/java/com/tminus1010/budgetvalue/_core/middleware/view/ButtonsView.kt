package com.tminus1010.budgetvalue._core.middleware.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.R
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thekhaeng.recyclerviewmargin.LayoutMarginDecoration
import com.tminus1010.budgetvalue._core.extensions.lifecycleOwner
import com.tminus1010.budgetvalue._core.middleware.presentation.ButtonVMItem
import com.tminus1010.budgetvalue.databinding.ItemButtonBinding
import com.tminus1010.tmcommonkotlin.view.extensions.toPX

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
        addItemDecoration(LayoutMarginDecoration(8.toPX(context)))
        adapter = object : LifecycleRVAdapter<GenViewHolder2<ItemButtonBinding>>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                GenViewHolder2(ItemButtonBinding.inflate(LayoutInflater.from(context), parent, false))

            override fun onViewAttachedToWindow(holder: GenViewHolder2<ItemButtonBinding>, lifecycle: LifecycleOwner) {
                holder.vb.root.lifecycleOwner = lifecycleOwner
                buttons[itemCount - 1 - holder.adapterPosition].bind(holder.vb.btnItem)
            }

            override fun getItemCount() = buttons.size
        }
    }
}