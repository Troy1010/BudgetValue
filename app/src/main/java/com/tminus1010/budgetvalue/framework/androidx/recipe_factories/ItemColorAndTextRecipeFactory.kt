package com.tminus1010.budgetvalue.framework.androidx.recipe_factories

import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.misc.tmTableView.ViewItemRecipe3
import com.tminus1010.budgetvalue.databinding.ItemColorAndTextBinding

fun Fragment.itemColorAndTextRF() = ItemColorAndTextRecipeFactory(requireContext())

class ItemColorAndTextRecipeFactory(private val context: Context) {
    private val inflate: (LayoutInflater) -> ItemColorAndTextBinding = ItemColorAndTextBinding::inflate
    fun create(s: String?, color: Int): IViewItemRecipe3 {
        return ViewItemRecipe3(context, inflate) { vb ->
            vb.colorBox.setBackgroundColor(color)
            vb.textview.text = s
        }
    }
}