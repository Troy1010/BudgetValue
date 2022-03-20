package com.tminus1010.budgetvalue.all_features.framework.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thekhaeng.recyclerviewmargin.LayoutMarginDecoration
import com.tminus1010.budgetvalue.all_features.presentation.model.ButtonVMItem
import com.tminus1010.budgetvalue.databinding.ItemButtonBinding
import com.tminus1010.tmcommonkotlin.misc.extensions.easySetHeight
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
    var shouldFitVertical = false
        set(value) {
            field = value; adapter?.notifyDataSetChanged()
        }

    init {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        addItemDecoration(LayoutMarginDecoration(8.toPX(context)))
        adapter = object : LifecycleRVAdapter2<GenViewHolder2<ItemButtonBinding>>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                GenViewHolder2(ItemButtonBinding.inflate(LayoutInflater.from(context), parent, false))
                    // TODO("This is a duct-tape solution b/c it is difficult to get a RV's items to fit vertically")
                    .also { if (shouldFitVertical) it.vb.root.easySetHeight((parent.height - 8.toPX(context) * (itemCount + 1) - 10.toPX(context)) / itemCount) }

            override fun getItemCount() = buttons.size
            override fun onLifecycleAttached(holder: GenViewHolder2<ItemButtonBinding>) {
                buttons[itemCount - 1 - holder.adapterPosition].bind(holder.vb.btnItem)
            }
        }
    }
}