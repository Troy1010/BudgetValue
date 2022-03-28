package com.tminus1010.budgetvalue.framework.android.recipe_factories

import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.misc.tmTableView.ViewItemRecipe3
import com.tminus1010.budgetvalue.databinding.ItemHeaderBinding
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import io.reactivex.rxjava3.core.Observable

fun Fragment.itemHeaderRF() = ItemHeaderRecipeFactory(requireContext())

class ItemHeaderRecipeFactory(private val context: Context) {
    private val inflate: (LayoutInflater) -> ItemHeaderBinding = ItemHeaderBinding::inflate
    fun create(s: String): IViewItemRecipe3 {
        return ViewItemRecipe3(context, inflate) { vb ->
            vb.textview.text = s
        }
    }

    fun create(d: Observable<String>): IViewItemRecipe3 {
        return ViewItemRecipe3(context, inflate) { vb ->
            vb.textview.bind(d) { text = it }
        }
    }
}