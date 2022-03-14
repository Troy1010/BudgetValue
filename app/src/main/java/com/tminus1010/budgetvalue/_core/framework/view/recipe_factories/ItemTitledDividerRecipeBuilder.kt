package com.tminus1010.budgetvalue._core.framework.view.recipe_factories

import android.content.Context
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue._core.framework.view.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue._core.framework.view.tmTableView3.ViewItemRecipe3__
import com.tminus1010.budgetvalue.databinding.ItemTitledDividerBinding
import com.tminus1010.tmcommonkotlin.view.extensions.toPX


fun Fragment.itemTitledDividerRB() = ItemTitledDividerRecipeBuilder(requireContext())

class ItemTitledDividerRecipeBuilder(private val context: Context) {
    private var styler: ((ItemTitledDividerBinding) -> Unit)? = null
    fun style(horizontalPaddingDP: Int): ItemTitledDividerRecipeBuilder {
        val horizontalPaddingPX = horizontalPaddingDP.toPX(context)
        styler = { vb ->
            vb.textview.setPadding(horizontalPaddingPX, 0, horizontalPaddingPX, 0)
            vb.textview.requestLayout()
        }
        return this
    }

    fun create(s: String): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, ItemTitledDividerBinding::inflate, ItemTitledDividerBinding::inflate, styler) { vb ->
            vb.textview.text = s
        }
    }
}