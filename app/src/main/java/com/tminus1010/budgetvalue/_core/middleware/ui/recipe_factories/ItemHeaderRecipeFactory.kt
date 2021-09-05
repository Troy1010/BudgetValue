package com.tminus1010.budgetvalue._core.middleware.ui.recipe_factories

import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue._core.extensions.bind
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.ViewItemRecipe3__
import com.tminus1010.budgetvalue.databinding.ItemHeaderBinding
import io.reactivex.rxjava3.core.Observable

fun Fragment.itemHeaderRF() = ItemHeaderRecipeFactory(requireContext())

class ItemHeaderRecipeFactory(private val context: Context) {
    private val inflate: (LayoutInflater) -> ItemHeaderBinding = ItemHeaderBinding::inflate
    fun create(s: String): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, inflate) { vb ->
            vb.textview.text = s
        }
    }

    fun create(d: Observable<String>): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, inflate) { vb ->
            vb.textview.bind(d) { text = it }
        }
    }
}