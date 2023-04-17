package com.tminus1010.buva.ui.transactions

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.android.viewBinding
import com.tminus1010.buva.databinding.FragTransactionsBinding
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionListFrag : Fragment(R.layout.frag_transactions) {
    private val vb by viewBinding(FragTransactionsBinding::bind)
    private val viewModel by activityViewModels<TransactionsVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Events
        viewModel.navToTransaction.observe(viewLifecycleOwner) { TransactionDetailsFrag.navTo(nav, it) }
        // # State
        vb.tvNoTransactionHistory.bind(viewModel.noTransactionsMsgVisibility) { visibility = it }
        vb.buttonsview.bind(viewModel.buttons) { buttons = it }
        vb.tmTableView.bind(viewModel.transactionVMItems) { it.bind(this) }
    }
}
