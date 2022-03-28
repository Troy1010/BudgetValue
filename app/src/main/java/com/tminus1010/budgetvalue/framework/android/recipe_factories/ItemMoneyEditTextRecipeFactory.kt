package com.tminus1010.budgetvalue.framework.android.recipe_factories

import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.all_layers.extensions.easyText2
import com.tminus1010.budgetvalue.framework.android.onDone
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.misc.tmTableView.ViewItemRecipe3
import com.tminus1010.budgetvalue.ui.all_features.model.CategoryAmountPresentationModel
import com.tminus1010.budgetvalue.databinding.ItemMoneyEditTextBinding
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import io.reactivex.rxjava3.core.Observable

fun Fragment.itemMoneyEditTextRF() = ItemMoneyEditTextRecipeFactory(requireContext())

class ItemMoneyEditTextRecipeFactory(private val context: Context) {
    private val inflate: (LayoutInflater) -> ItemMoneyEditTextBinding = ItemMoneyEditTextBinding::inflate
    fun create(s: String): IViewItemRecipe3 {
        return ViewItemRecipe3(context, inflate) { vb ->
            vb.moneyedittext.easyText2 = s
        }
    }

    fun create(d: Observable<String>?): IViewItemRecipe3 {
        return ViewItemRecipe3(context, inflate) { vb ->
            if (d == null) return@ViewItemRecipe3
            vb.moneyedittext.bind(d) { easyText2 = it }
        }
    }

    fun create(d: Observable<String>?, onDone: (String) -> Unit): IViewItemRecipe3 {
        return ViewItemRecipe3(context, inflate) { vb ->
            vb.moneyedittext.onDone(onDone)
            if (d == null) return@ViewItemRecipe3
            vb.moneyedittext.bind(d) { easyText2 = it }
        }
    }

    fun create(categoryAmountPresentationModel: CategoryAmountPresentationModel): IViewItemRecipe3 {
        return ViewItemRecipe3(context, inflate) { vb ->
            categoryAmountPresentationModel.bind(vb.moneyedittext)
        }
    }
}