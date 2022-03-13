package com.tminus1010.budgetvalue._core.presentation.model

import android.content.Context
import android.widget.Button
import com.tminus1010.budgetvalue._core.framework.view.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue._core.framework.view.tmTableView3.ViewItemRecipe3__
import com.tminus1010.budgetvalue.budgeted.presentation.IHasToViewItemRecipe
import com.tminus1010.budgetvalue.databinding.ItemCategoryBtnBinding
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow

data class ButtonVMItem2(
    val title: String? = null,
    val titleObservable: Observable<String>? = null,
    val isEnabled: Observable<Boolean>? = null,
    val isEnabled2: Flow<Boolean>? = null,
    val alpha: Flow<Float>? = null,
    val onLongClick: (() -> Unit)? = null,
    val onClick: () -> Unit,
) : IHasToViewItemRecipe {
    fun bind(button: Button) {
        button.text = title
        if (titleObservable != null)
            button.bind(titleObservable) { text = title }
        if (isEnabled != null)
            button.bind(isEnabled) { button.isEnabled = it }
        if (isEnabled2 != null)
            button.bind(isEnabled2) { button.isEnabled = it }
        if (alpha != null)
            button.bind(alpha) { alpha = it }
        button.setOnClickListener { onClick() }
        onLongClick?.also { button.setOnLongClickListener { it(); true } }
    }

    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, ItemCategoryBtnBinding::inflate) { vb ->
            bind(vb.btnCategory)
        }
    }
}