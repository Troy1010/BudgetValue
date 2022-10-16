package com.tminus1010.buva.ui.transactions

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.android.viewBinding
import com.tminus1010.buva.environment.MoshiWithCategoriesProvider
import com.tminus1010.buva.databinding.FragTransactionsBinding
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TransactionListFrag : Fragment(R.layout.frag_transactions) {
    private val vb by viewBinding(FragTransactionsBinding::bind)
    private val viewModel by activityViewModels<TransactionsVM>()

    @Inject
    lateinit var moshiWithCategoriesProvider: MoshiWithCategoriesProvider
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Events
        viewModel.navToTransaction.observe(viewLifecycleOwner) { TransactionDetailsFrag.navTo(nav, it, moshiWithCategoriesProvider) }
        // # State
        vb.tvNoTransactionHistory.bind(viewModel.noTransactionsMsgVisibility) { visibility = it }
        vb.buttonsview.bind(viewModel.buttons) { buttons = it }
        vb.tmTableView.bind(viewModel.transactionVMItems) { it.bind(this) }
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.transactionsFrag)
        }
    }
}
