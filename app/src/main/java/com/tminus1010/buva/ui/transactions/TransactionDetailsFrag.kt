package com.tminus1010.buva.ui.transactions

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.all_layers.android.viewBinding
import com.tminus1010.buva.databinding.FragTransactionDetailsBinding
import com.tminus1010.buva.domain.Transaction
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionDetailsFrag : Fragment(R.layout.frag_transaction_details) {
    private val vb by viewBinding(FragTransactionDetailsBinding::bind)
    private val viewModel by viewModels<TransactionDetailsVM>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # State
        vb.buttonsview.bind(viewModel.buttons) { buttons = it }
        vb.tmTableViewTitle.bind(viewModel.transactionInfoTableView) { it.bind(this) }
        vb.tmTableView.bind(viewModel.transactionCategoryAmountsTableView) { it.bind(this) }
    }

    companion object {
        fun navTo(nav: NavController, transaction: Transaction) {
            nav.navigate(R.id.transactionFrag, Bundle().apply {
                putParcelable(KEY1, transaction)
            })
        }
    }
}