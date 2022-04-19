package com.tminus1010.buva.ui.transactions

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.data.service.MoshiWithCategoriesProvider
import com.tminus1010.buva.databinding.FragTransactionsBinding
import com.tminus1010.buva.framework.android.viewBinding
import com.tminus1010.tmcommonkotlin.androidx.ShowAlertDialog
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
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
        // # Setup
        viewModel.showAlertDialog.onNext(ShowAlertDialog(requireActivity()))
        // # Events
        viewModel.navToTransaction.observe(viewLifecycleOwner) { TransactionDetailsFrag.navTo(nav, it, moshiWithCategoriesProvider) }
        // # State
        vb.buttonsview.bind(viewModel.buttons) { buttons = it }
        vb.tmTableView.bind(viewModel.transactionVMItems) { it.bind(this) }
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.transactionsFrag)
        }
    }
}
