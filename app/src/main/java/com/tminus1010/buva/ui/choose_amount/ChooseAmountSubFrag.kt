package com.tminus1010.buva.ui.choose_amount

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.extensions.easyEmit
import com.tminus1010.buva.all_layers.extensions.onClick
import com.tminus1010.buva.databinding.SubfragChooseAmountBinding
import com.tminus1010.buva.framework.android.onDone
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseAmountSubFrag : Fragment(R.layout.subfrag_choose_amount) {
    lateinit var vb: SubfragChooseAmountBinding
    val viewModel by viewModels<ChooseAmountVM>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = SubfragChooseAmountBinding.bind(view)
        // # Setup View
        vb.tmTableViewPlusMinus.bind(viewModel.plusMinusButtonsTableView) { it.bind(this) }
        vb.moneyEditText.run { viewModel.amountMenuVMItem.bind(this) }
        // # State
        vb.moneyEditText.bind(viewModel.amount) { setText(it) }
        // # Bind User Intents
        vb.moneyEditText.onDone { viewModel.userSetAmount.easyEmit(it) }
        vb.buttonSubmit.onClick { viewModel.userSubmitAmount.easyEmit(Unit) }
    }
}