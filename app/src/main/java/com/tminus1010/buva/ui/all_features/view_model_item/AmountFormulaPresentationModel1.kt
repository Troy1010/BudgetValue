package com.tminus1010.buva.ui.all_features.view_model_item

import android.content.Context
import com.tminus1010.buva.all_layers.extensions.easyText2
import com.tminus1010.buva.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.buva.framework.android.onDone
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.misc.tmTableView.ViewItemRecipe3
import com.tminus1010.buva.databinding.ItemAmountFormulaBinding
import com.tminus1010.buva.domain.AmountFormula
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IHasToViewItemRecipe
import com.tminus1010.tmcommonkotlin.view.extensions.easyVisibility
import kotlinx.coroutines.flow.StateFlow

data class AmountFormulaPresentationModel1(
    private val amountFormula: StateFlow<AmountFormula>,
    private val onNewAmountFormula: (AmountFormula) -> Unit,
) : IHasToViewItemRecipe {
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
            }
        if (newAmountFormula != amountFormula) onNewAmountFormula(newAmountFormula)
    }

    fun bind(vb: ItemAmountFormulaBinding) {
        vb.moneyEditText.bind(amountFormula) { easyText2 = it.toDisplayStr() }
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