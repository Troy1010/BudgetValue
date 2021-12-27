package com.tminus1010.budgetvalue.transactions.presentation.models

import android.content.Context
import com.tminus1010.budgetvalue._core.all.extensions.bind
import com.tminus1010.budgetvalue._core.framework.view.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue._core.framework.view.tmTableView3.ViewItemRecipe3__
import com.tminus1010.budgetvalue.budgeted.presentation.IHasToViewItemRecipe
import com.tminus1010.budgetvalue.databinding.ItemTextViewBinding
import com.tminus1010.tmcommonkotlin.tuple.Box
import io.reactivex.rxjava3.core.Observable

class TextPresentationModel(
    val text1: String? = null,
    val text2: Observable<Box<String?>>? = null,
    val onClick: (() -> Unit)? = null,
) : IHasToViewItemRecipe {
    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, ItemTextViewBinding::inflate) { vb ->
            vb.textview.text = text1
            text2?.also { vb.textview.bind(text2) { text = it.first } }
            vb.textview.setOnClickListener { onClick?.invoke() }
        }
    }
}