package com.tminus1010.budgetvalue.transactions.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.all.extensions.easyEmit
import com.tminus1010.budgetvalue._core.all.extensions.onClick
import com.tminus1010.budgetvalue._core.framework.view.onDone
import com.tminus1010.budgetvalue.databinding.SubfragChooseAmountBinding
import com.tminus1010.budgetvalue.transactions.presentation.ChooseAmountVM
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseAmountSubFrag : Fragment(R.layout.subfrag_choose_amount) {
    lateinit var vb: SubfragChooseAmountBinding
    val chooseAmountVM by viewModels<ChooseAmountVM>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = SubfragChooseAmountBinding.bind(view)
        // # Setup View
        vb.tmTableViewPlusMinus.initialize(
            recipeGrid = chooseAmountVM.buttons.map { it.map { it.toViewItemRecipe(requireContext()) } },
            shouldFitItemWidthsInsideTable = true,
        )
        vb.moneyEditText.run { chooseAmountVM.amountMenuPresentationModel.bind(this) }
        // # Bind Presentation State
        vb.moneyEditText.bind(chooseAmountVM.amount) { setText(it) }
        // # Bind User Intents
        vb.moneyEditText.onDone { chooseAmountVM.userSetAmount.easyEmit(it) }
        vb.buttonSubmit.onClick { chooseAmountVM.userSubmitAmount.easyEmit(Unit) }
    }
}