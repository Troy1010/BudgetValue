package com.tminus1010.budgetvalue._core.middleware.ui.recipe_factories

import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue._core.extensions.bind
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.ViewItemRecipe3__
import com.tminus1010.budgetvalue.databinding.ItemTextViewBinding
import io.reactivex.rxjava3.core.Observable

fun Fragment.itemTextViewRF() = ItemTextViewRecipeFactory(requireContext())

class ItemTextViewRecipeFactory(private val context: Context) {
    private val inflate: (LayoutInflater) -> ItemTextViewBinding = ItemTextViewBinding::inflate
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

    fun create(s: String, onClick: () -> Unit): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, inflate) { vb ->
            vb.textview.text = s
            vb.textview.setOnClickListener { onClick() }
        }
    }
}