package com.tminus1010.buva.ui.all_features.view_model_item

import android.content.Context
import androidx.lifecycle.LiveData
import com.tminus1010.buva.all_layers.extensions.easyText3
import com.tminus1010.buva.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.buva.databinding.ItemAmountFormulaBinding
import com.tminus1010.buva.domain.AmountFormula
import com.tminus1010.buva.all_layers.android.onDone
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipeFactory
import com.tminus1010.tmcommonkotlin.customviews.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.easyVisibility

data class AmountFormulaPresentationModel1(
    private val amountFormula: LiveData<AmountFormula>,
    private val onNewAmountFormula: (AmountFormula) -> Unit,
) : ViewItemRecipeFactory {
    fun userToggleIsPercentage() {
        when (val amountFormula = amountFormula.value) {
            is AmountFormula.Value -> onNewAmountFormula(AmountFormula.Percentage(amountFormula.amount))
            is AmountFormula.Percentage -> onNewAmountFormula(AmountFormula.Value(amountFormula.percentage))
        }
    }

    fun userSetValue(s: String) {
        val newAmountFormula =
            when (amountFormula.value) {
                is AmountFormula.Percentage -> AmountFormula.Percentage(s.toBigDecimal())
                is AmountFormula.Value -> AmountFormula.Value(s.toMoneyBigDecimal())
                else -> error("Unhandled")
            }
        if (newAmountFormula != amountFormula) onNewAmountFormula(newAmountFormula)
    }

    fun bind(vb: ItemAmountFormulaBinding) {
        vb.moneyEditText.bind(amountFormula) { easyText3 = it.toDisplayStr() }
        vb.tvPercentage.bind(amountFormula) { easyVisibility = it is AmountFormula.Percentage }
        vb.moneyEditText.onDone { userSetValue(it) }
        vb.root.bind(amountFormula) { amountFormula ->
            MenuVMItems(
                if (amountFormula is AmountFormula.Value)
                    MenuVMItem(
                        title = "Percentage",
                        onClick = { userToggleIsPercentage() },
                    )
                else null,
                if (amountFormula is AmountFormula.Percentage)
                    MenuVMItem(
                        title = "No Percentage",
                        onClick = { userToggleIsPercentage() },
                    )
                else null,
            ).bind(this)
        }
    }

    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3(context, ItemAmountFormulaBinding::inflate, _bind = ::bind)
    }
}