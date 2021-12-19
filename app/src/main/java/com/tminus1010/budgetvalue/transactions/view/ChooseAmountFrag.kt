package com.tminus1010.budgetvalue.transactions.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.all.extensions.bind
import com.tminus1010.budgetvalue._core.all.extensions.easyEmit
import com.tminus1010.budgetvalue._core.framework.view.onDone
import com.tminus1010.budgetvalue.databinding.FragChooseAmountBinding
import com.tminus1010.budgetvalue.transactions.presentation.ChooseAmountVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseAmountFrag : Fragment(R.layout.frag_choose_amount) {
    lateinit var vb: FragChooseAmountBinding
    val chooseAmountVM by viewModels<ChooseAmountVM>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragChooseAmountBinding.bind(view)
        // # Setup View
        vb.tmTableViewPlusMinus.initialize(
            chooseAmountVM.buttons.map { it.map { it.toViewItemRecipe(requireContext()) } },
            shouldFitItemWidthsInsideTable = true
        )
        // # Bind Presentation State
        vb.moneyEditText.bind(chooseAmountVM.amount) { setText(it) }
        // # Bind User Intents
        vb.moneyEditText.onDone { chooseAmountVM.userSetAmount.easyEmit(it) }
    }
}