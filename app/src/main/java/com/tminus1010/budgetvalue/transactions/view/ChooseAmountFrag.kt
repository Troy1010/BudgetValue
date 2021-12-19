package com.tminus1010.budgetvalue.transactions.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.databinding.FragChooseAmountBinding
import com.tminus1010.budgetvalue.transactions.presentation.ChooseAmountVM

class ChooseAmountFrag : Fragment(R.layout.frag_choose_amount) {
    lateinit var vb: FragChooseAmountBinding
    val chooseAmountVM by viewModels<ChooseAmountVM>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragChooseAmountBinding.bind(view)
        //
        vb.tmTableViewPlusMinus.initialize(
            chooseAmountVM.buttons.map { it.map { it.toViewItemRecipe(requireContext()) } },
            shouldFitItemWidthsInsideTable = true
        )
    }
}