package com.tminus1010.budgetvalue._core.middleware.view.recipe_factories

import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue._core.extensions.bind
import com.tminus1010.budgetvalue._core.extensions.easyText
import com.tminus1010.budgetvalue._core.middleware.view.onDone
import com.tminus1010.budgetvalue._core.middleware.view.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue._core.middleware.view.tmTableView3.ViewItemRecipe3__
import com.tminus1010.budgetvalue.databinding.ItemMoneyEditTextBinding
import io.reactivex.rxjava3.core.Observable

fun Fragment.itemMoneyEditTextRF() = ItemMoneyEditTextRecipeFactory(requireContext())

class ItemMoneyEditTextRecipeFactory(private val context: Context) {
    private val inflate: (LayoutInflater) -> ItemMoneyEditTextBinding = ItemMoneyEditTextBinding::inflate
    fun create(s: String): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, inflate) { vb ->
            vb.moneyedittext.easyText = s
        }
    }

    fun create(d: Observable<String>): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, inflate) { vb ->
            vb.moneyedittext.bind(d) { easyText = it }
        }
    }

    fun create(d: Observable<String>, lambda: (String) -> Unit): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, inflate) { vb ->
            vb.moneyedittext.bind(d) { easyText = it }
            vb.moneyedittext.onDone(lambda)
        }
    }
}