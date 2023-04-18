package com.tminus1010.buva.ui.all_features.view_model_item

import android.content.Context
import com.tminus1010.buva.all_layers.extensions.easyText2
import com.tminus1010.tmcommonkotlin.customviews.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipe3
import com.tminus1010.buva.databinding.ItemAmountFormulaBinding
import com.tminus1010.buva.domain.AmountFormula
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import com.tminus1010.tmcommonkotlin.customviews.ViewItemRecipeFactory
import com.tminus1010.tmcommonkotlin.view.extensions.easyVisibility
import kotlinx.coroutines.flow.Flow

data class AmountFormulaPresentationModel2(
    private val amountFormula: Flow<AmountFormula>,
) : ViewItemRecipeFactory {
    fun bind(vb: ItemAmountFormulaBinding) {
        vb.root.isEnabled = false
        vb.moneyEditText.isEnabled = false
        vb.moneyEditText.bind(amountFormula) { easyText2 = it.toDisplayStr() }
        vb.tvPercentage.bind(amountFormula) { easyVisibility = it is AmountFormula.Percentage }
    }

    override fun toViewItemRecipe(context: Context): IViewItemRecipe3 {
        return ViewItemRecipe3(context, ItemAmountFormulaBinding::inflate, _bind = ::bind)
    }
}