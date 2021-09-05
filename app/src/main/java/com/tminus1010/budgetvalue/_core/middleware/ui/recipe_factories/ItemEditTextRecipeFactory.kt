package com.tminus1010.budgetvalue._core.middleware.ui.recipe_factories

import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue._core.extensions.add
import com.tminus1010.budgetvalue._core.extensions.bind
import com.tminus1010.budgetvalue._core.extensions.easyText
import com.tminus1010.budgetvalue._core.middleware.ui.MenuVMItem
import com.tminus1010.budgetvalue._core.middleware.ui.onDone
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.ViewItemRecipe3__
import com.tminus1010.budgetvalue.databinding.ItemEditTextBinding
import io.reactivex.rxjava3.core.Observable

fun Fragment.itemEditTextRF() = ItemEditTextRecipeFactory(requireContext())

class ItemEditTextRecipeFactory(private val context: Context) {
    private val inflate: (LayoutInflater) -> ItemEditTextBinding = ItemEditTextBinding::inflate
    fun create(d: Observable<String>, lambda: (String) -> Unit, menuItems: List<MenuVMItem>): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, inflate) { vb ->
            vb.edittext.bind(d) { easyText = it }
            vb.edittext.onDone(lambda)
            vb.edittext.setOnCreateContextMenuListener { menu, _, _ ->
                menu.add(menuItems)
            }
        }
    }
}