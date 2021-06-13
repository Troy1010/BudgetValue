package com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3

import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue._core.extensions.bind
import com.tminus1010.budgetvalue._core.extensions.easyText
import com.tminus1010.budgetvalue.databinding.ItemHeaderBinding
import com.tminus1010.budgetvalue.databinding.ItemTextViewBinding
import com.tminus1010.budgetvalue.databinding.ItemTitledDividerBinding
import io.reactivex.rxjava3.core.Observable

val Fragment.itemTitledDividerBindingRF: ViewItemRecipeFactory3<ItemTitledDividerBinding, String>
    get() = ViewItemRecipeFactory3(
        { ItemTitledDividerBinding.inflate(LayoutInflater.from(requireContext())) },
        { d: String, vb, _ -> vb.textview.easyText = d }
    )

val Fragment.itemTextViewBindingRF: ViewItemRecipeFactory3<ItemTextViewBinding, String>
    get() = ViewItemRecipeFactory3(
        { ItemTextViewBinding.inflate(LayoutInflater.from(requireContext())) },
        { d: String, vb, _ -> vb.textview.easyText = d }
    )

val Fragment.itemHeaderBindingRF: ViewItemRecipeFactory3<ItemHeaderBinding, String>
    get() = ViewItemRecipeFactory3(
        { ItemHeaderBinding.inflate(LayoutInflater.from(requireContext())) },
        { d: String, vb, _ -> vb.textview.easyText = d }
    )

val Fragment.itemTextViewBindingLRF: ViewItemRecipeFactory3<ItemTextViewBinding, Observable<String>>
    get() = ViewItemRecipeFactory3(
        { ItemTextViewBinding.inflate(LayoutInflater.from(requireContext())) },
        { d, vb, lifecycle -> vb.textview.bind(d, lifecycle) { easyText = it } }
    )
