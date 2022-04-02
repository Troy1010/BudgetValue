package com.tminus1010.budgetvalue.ui.transactions

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.all_layers.KEY1
import com.tminus1010.budgetvalue.data.service.MoshiWithCategoriesProvider
import com.tminus1010.budgetvalue.databinding.FragTransactionBinding
import com.tminus1010.budgetvalue.domain.Transaction
import com.tminus1010.budgetvalue.framework.android.viewBinding
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionDetailsFrag : Fragment(R.layout.frag_transaction) {
    private val vb by viewBinding(FragTransactionBinding::bind)
    private val viewModel by viewModels<TransactionDetailsVM>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Events
        viewModel.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        // # State
        vb.buttonsview.bind(viewModel.buttons) { buttons = it }
        vb.tmTableViewTitle.bind(viewModel.transactionInfoTableView) { it.bind(this) }
        vb.tmTableView.bind(viewModel.transactionCategoryAmountsTableView) { it.bind(this) }
    }

    companion object {
        fun navTo(nav: NavController, transaction: Transaction, moshiWithCategoriesProvider: MoshiWithCategoriesProvider) {
            nav.navigate(R.id.transactionFrag, Bundle().apply {
                putString(KEY1, moshiWithCategoriesProvider.moshi.toJson(transaction))
            })
        }
    }
}