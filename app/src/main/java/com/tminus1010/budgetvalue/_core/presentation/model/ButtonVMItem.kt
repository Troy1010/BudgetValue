package com.tminus1010.budgetvalue._core.presentation.model

import android.content.Context
import android.widget.Button
import com.tminus1010.budgetvalue._core.framework.view.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue._core.framework.view.tmTableView3.ViewItemRecipe3__
import com.tminus1010.budgetvalue.budgeted.presentation.IHasToViewItemRecipe
import com.tminus1010.budgetvalue.databinding.ItemButtonBinding
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.misc.extensions.lifecycleOwner
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow

data class ButtonVMItem(
    val title: String? = null,
    val titleObservable: Observable<String>? = null,
    val isEnabled: Observable<Boolean>? = null,
    val isEnabled2: Flow<Boolean>? = null,
    val onLongClick: (() -> Unit)? = null,
    val onClick: () -> Unit,
) : IHasToViewItemRecipe {
    fun bind(button: Button) = button.apply {
        if (titleObservable != null)
            bind(titleObservable) { text = title }
        if (text != null)
            text = title
        setOnClickListener { onClick() }
        onLongClick?.also { setOnLongClickListener { it(); true } }
        this@ButtonVMItem.isEnabled?.observe(button.lifecycleOwner!!) { isEnabled = it }
            ?: run { isEnabled = true }
        isEnabled2?.observe(button.lifecycleOwner!!) { isEnabled = it }
            ?: run { isEnabled = true }
    }

    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, ItemButtonBinding::inflate) { vb ->
            bind(vb.btnItem)
        }
    }
}