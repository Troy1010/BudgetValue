package com.tminus1010.budgetvalue._core.presentation.model

import android.content.Context
import com.tminus1010.budgetvalue._core.all_layers.extensions.easyText2
import com.tminus1010.budgetvalue._core.framework.view.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue._core.framework.view.tmTableView3.ViewItemRecipe3__
import com.tminus1010.budgetvalue.budgeted.presentation.IHasToViewItemRecipe
import com.tminus1010.budgetvalue.databinding.ItemAmountFormulaBinding
import com.tminus1010.budgetvalue.transactions.app.AmountFormula
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.easyVisibility
import kotlinx.coroutines.flow.Flow

data class AmountFormulaPresentationModel2(
    private val amountFormula: Flow<AmountFormula>,
) : IHasToViewItemRecipe {
    fun bind(vb: ItemAmountFormulaBinding) {
        vb.root.isEnabled = false
        vb.moneyEditText.isEnabled = false
        vb.moneyEditText.bind(amountFormula) { easyText2 = it.toDisplayStr() }
        vb.tvPercentage.bind(amountFormula) { easyVisibility = it is AmountFormula.Percentage }
    }

    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3__(context, ItemAmountFormulaBinding::inflate, _bind = ::bind)
    }
}