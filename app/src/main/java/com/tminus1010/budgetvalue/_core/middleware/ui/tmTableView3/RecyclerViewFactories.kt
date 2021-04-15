package com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.viewItemRecipeFactories

import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.ViewItemRecipeFactory3
import com.tminus1010.budgetvalue._core.ui.data_binding.bindText
import com.tminus1010.budgetvalue.databinding.ItemHeaderBinding
import com.tminus1010.budgetvalue.databinding.ItemTextViewBinding
import com.tminus1010.budgetvalue.databinding.ItemTitledDividerBinding

val Fragment.itemTitledDividerBindingRF: ViewItemRecipeFactory3<ItemTitledDividerBinding, String>
    get() = ViewItemRecipeFactory3(
        { ItemTitledDividerBinding.inflate(LayoutInflater.from(requireContext())) },
        { d: String, vb, _ -> vb.textview.text = d }
    )

val Fragment.itemTextViewBindingRF: ViewItemRecipeFactory3<ItemTextViewBinding, String>
    get() = ViewItemRecipeFactory3(
        { ItemTextViewBinding.inflate(LayoutInflater.from(requireContext())) },
        { d: String, vb, _ -> vb.textview.text = d }
    )

val Fragment.itemHeaderBindingRF: ViewItemRecipeFactory3<ItemHeaderBinding, String>
    get() = ViewItemRecipeFactory3(
        { ItemHeaderBinding.inflate(LayoutInflater.from(requireContext())) },
        { d: String, vb, _ -> vb.textview .text = d }
    )

val Fragment.itemTextViewBindingLRF: ViewItemRecipeFactory3<ItemTextViewBinding, LiveData<String>>
    get() = ViewItemRecipeFactory3(
        { ItemTextViewBinding.inflate(LayoutInflater.from(requireContext())) },
        { d: LiveData<String>, vb, lifecycle -> vb.textview.bindText(d, lifecycle) }
    )
