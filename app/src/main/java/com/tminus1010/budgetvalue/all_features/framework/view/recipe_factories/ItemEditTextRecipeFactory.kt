package com.tminus1010.budgetvalue.all_features.framework.view.recipe_factories

import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.add
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.easyText2
import com.tminus1010.budgetvalue.all_features.presentation.model.MenuVMItem
import com.tminus1010.budgetvalue.all_features.framework.view.onDone
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.misc.tmTableView.ViewItemRecipe3
import com.tminus1010.budgetvalue.databinding.ItemEditTextBinding
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import io.reactivex.rxjava3.core.Observable

fun Fragment.itemEditTextRF() = ItemEditTextRecipeFactory(requireContext())

class ItemEditTextRecipeFactory(private val context: Context) {
    private val inflate: (LayoutInflater) -> ItemEditTextBinding = ItemEditTextBinding::inflate
    fun create(d: Observable<String>, lambda: (String) -> Unit, menuItems: List<MenuVMItem>): IViewItemRecipe3 {
        return ViewItemRecipe3(context, inflate) { vb ->
            vb.edittext.bind(d) { easyText2 = it }
            vb.edittext.onDone(lambda)
            vb.edittext.setOnCreateContextMenuListener { menu, _, _ ->
                menu.add(menuItems)
            }
        }
    }

    fun create(s: String, lambda: (String) -> Unit): IViewItemRecipe3 {
        return ViewItemRecipe3(context, inflate) { vb ->
            vb.edittext.easyText2 = s
            vb.edittext.onDone(lambda)
        }
    }

    fun create(lambda: (String) -> Unit): IViewItemRecipe3 {
        return ViewItemRecipe3(context, inflate) { vb ->
            vb.edittext.onDone(lambda)
        }
    }
}