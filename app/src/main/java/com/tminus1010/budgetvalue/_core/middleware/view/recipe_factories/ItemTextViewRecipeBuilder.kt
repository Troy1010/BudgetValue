package com.tminus1010.budgetvalue._core.middleware.view.recipe_factories

import android.content.Context
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.extensions.getColorByAttr
import com.tminus1010.budgetvalue._core.middleware.view.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue._core.middleware.view.tmTableView3.ViewItemRecipe3__
import com.tminus1010.budgetvalue.databinding.ItemTextViewBinding
import com.tminus1010.tmcommonkotlin.view.extensions.toPX
import io.reactivex.rxjava3.core.Observable

fun Fragment.itemTextViewRB() = ItemTextViewRecipeBuilder(requireContext())

class ItemTextViewRecipeBuilder(private val context: Context) {
    private var styler: ((ItemTextViewBinding) -> Unit)? = null
    fun style(horizontalPaddingDP: Int): ItemTextViewRecipeBuilder {
        val horizontalPaddingPX = horizontalPaddingDP.toPX(context)
        styler = { vb ->
            vb.textview.setPadding(horizontalPaddingPX, 0, horizontalPaddingPX, 0)
            vb.textview.requestLayout()
        }
        return this
    }

    fun create(s: String?): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, ItemTextViewBinding::inflate, styler) { vb ->
            vb.textview.text = s
        }
    }

    fun create(d: Observable<String>): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, ItemTextViewBinding::inflate, styler) { vb ->
            vb.textview.bind(d) { text = it }
        }
    }

    fun create(s: String, onClick: () -> Unit): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, ItemTextViewBinding::inflate, styler) { vb ->
            vb.textview.text = s
            vb.textview.setOnClickListener { onClick() }
        }
    }

    fun create(s: String, context: Context, highlighted: Boolean, onClick: () -> Unit): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, ItemTextViewBinding::inflate, styler) { vb ->
            vb.textview.text = s
            vb.textview.setBackgroundColor(
                context.theme.getColorByAttr(
                    if (highlighted) R.attr.colorSecondary else R.attr.colorBackground
                )
            )
            vb.textview.setOnClickListener { onClick() }
        }
    }
}