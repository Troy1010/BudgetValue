package com.tminus1010.budgetvalue._core.framework.view.recipe_factories

import android.content.Context
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.all.extensions.bind
import com.tminus1010.budgetvalue._core.all.extensions.getColorByAttr
import com.tminus1010.budgetvalue._core.framework.view.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue._core.framework.view.tmTableView3.ViewItemRecipe3__
import com.tminus1010.budgetvalue._core.presentation.model.AmountPresentationModel
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

    @JvmName("createValidatedStringVMItem")
    fun create(amountPresentationModel: Observable<AmountPresentationModel>?): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, ItemTextViewBinding::inflate, styler) { vb ->
            if (amountPresentationModel == null) return@ViewItemRecipe3__
            vb.textview.bind(amountPresentationModel) {
                text = it.s
                setTextColor(
                    context.theme.getColorByAttr(
                        if (it.isValid)
                            R.attr.colorOnBackground
                        else
                            R.attr.colorOnError
                    )
                )
            }
        }
    }

    fun create(amountPresentationModel: AmountPresentationModel): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, ItemTextViewBinding::inflate, styler) { vb ->
            vb.textview.text = amountPresentationModel.s
            vb.textview.setTextColor(
                context.theme.getColorByAttr(
                    if (amountPresentationModel.isValid)
                        R.attr.colorOnBackground
                    else
                        R.attr.colorOnError
                )
            )
        }
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