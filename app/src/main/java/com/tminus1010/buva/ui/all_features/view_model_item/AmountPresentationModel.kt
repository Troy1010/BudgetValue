package com.tminus1010.buva.ui.all_features.view_model_item

import android.content.Context
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.android.onDone
import com.tminus1010.buva.all_layers.extensions.getColorByAttr
import com.tminus1010.buva.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.buva.databinding.ItemEditTextBinding
import com.tminus1010.buva.databinding.ItemTextViewBinding
import com.tminus1010.buva.domain.ValidationResult
import com.tminus1010.tmcommonkotlin.customviews.IHasToViewItemRecipe
import com.tminus1010.tmcommonkotlin.customviews.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipe3
import java.math.BigDecimal

class AmountPresentationModel(
    val bigDecimal: BigDecimal?,
    private val validation: (BigDecimal) -> ValidationResult = { ValidationResult.Success },
    private val onNewAmount: ((BigDecimal?) -> Unit)? = null,
    private val menuVMItems: MenuVMItems? = null,
) : IHasToViewItemRecipe {
    val s get() = bigDecimal?.toString()
    val validationResult get() = validation(bigDecimal ?: BigDecimal.ZERO)
    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return if (onNewAmount != null)
            ViewItemRecipe3(context, ItemEditTextBinding::inflate) { vb ->
                menuVMItems?.bind(vb.root)
                vb.edittext.setText(s)
                vb.edittext.setTextColor(
                    context.theme.getColorByAttr(
                        when (validationResult) {
                            ValidationResult.Success ->
                                R.attr.colorOnBackground
                            ValidationResult.Warning ->
                                R.attr.colorOnWarning
                            ValidationResult.Failure ->
                                R.attr.colorOnError
                        }
                    )
                )
                vb.edittext.onDone { onNewAmount!!(it.ifEmpty { null }?.toMoneyBigDecimal()) }
            }
        else
            ViewItemRecipe3(context, ItemTextViewBinding::inflate) { vb ->
                menuVMItems?.bind(vb.root)
                vb.textview.text = s
                vb.textview.setTextColor(
                    context.theme.getColorByAttr(
                        when (validationResult) {
                            ValidationResult.Success ->
                                R.attr.colorOnBackground
                            ValidationResult.Warning ->
                                R.attr.colorOnWarning
                            ValidationResult.Failure ->
                                R.attr.colorOnError
                        }
                    )
                )
            }
    }
}