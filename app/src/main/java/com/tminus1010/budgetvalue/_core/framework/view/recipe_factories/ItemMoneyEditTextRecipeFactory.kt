package com.tminus1010.budgetvalue._core.framework.view.recipe_factories

import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue._core.all.extensions.bind
import com.tminus1010.budgetvalue._core.all.extensions.easyText
import com.tminus1010.budgetvalue._core.framework.view.onDone
import com.tminus1010.budgetvalue._core.framework.view.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue._core.framework.view.tmTableView3.ViewItemRecipe3__
import com.tminus1010.budgetvalue._core.presentation.model.CategoryAmountPresentationModel
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

    fun create(d: Observable<String>?): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, inflate) { vb ->
            if (d == null) return@ViewItemRecipe3__
            vb.moneyedittext.bind(d) { easyText = it }
        }
    }

    fun create(d: Observable<String>?, onDone: (String) -> Unit): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, inflate) { vb ->
            vb.moneyedittext.onDone(onDone)
            if (d == null) return@ViewItemRecipe3__
            vb.moneyedittext.bind(d) { easyText = it }
        }
    }

    fun create(categoryAmountPresentationModel: CategoryAmountPresentationModel): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, inflate) { vb ->
            categoryAmountPresentationModel.bind(vb.moneyedittext)
        }
    }
}