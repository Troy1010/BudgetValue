package com.tminus1010.budgetvalue._core.framework.view.recipe_factories

import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue._core.all.extensions.bind
import com.tminus1010.budgetvalue._core.framework.view.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue._core.framework.view.tmTableView3.ViewItemRecipe3__
import com.tminus1010.budgetvalue.databinding.ItemCheckboxBinding
import io.reactivex.rxjava3.core.Observable


fun Fragment.itemCheckboxRF() = ItemCheckboxRecipeFactory(requireContext())

class ItemCheckboxRecipeFactory(private val context: Context) {
    private val inflate: (LayoutInflater) -> ItemCheckboxBinding = ItemCheckboxBinding::inflate
    fun create(shouldDisable: Observable<Boolean>, s: String, lambda: (String) -> Unit): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, inflate) { vb ->
            vb.checkbox.bind(shouldDisable) { isChecked = it; isEnabled = !it }
            vb.checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) lambda(s)
            }
        }
    }

    fun create(initialIsChecked: Boolean, lambda: (Boolean) -> Unit): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, inflate) { vb ->
            vb.checkbox.isChecked = initialIsChecked
            vb.checkbox.setOnCheckedChangeListener { _, isChecked ->
                lambda(isChecked)
            }
        }
    }
}