package com.tminus1010.budgetvalue._unrestructured.transactions.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._unrestructured.transactions.presentation.TransactionVM
import com.tminus1010.budgetvalue.all_layers.KEY1
import com.tminus1010.budgetvalue.all_layers.extensions.onNext
import com.tminus1010.budgetvalue.data.service.MoshiWithCategoriesProvider
import com.tminus1010.budgetvalue.databinding.FragTransactionBinding
import com.tminus1010.budgetvalue.domain.Transaction
import com.tminus1010.budgetvalue.framework.android.viewBinding
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TransactionFrag : Fragment(R.layout.frag_transaction) {
    private val vb by viewBinding(FragTransactionBinding::bind)
    private val viewModel by viewModels<TransactionVM>()

    @Inject
    lateinit var moshiWithCategoriesProvider: MoshiWithCategoriesProvider
    private val transaction by lazy { moshiWithCategoriesProvider.moshi.fromJson<Transaction>(requireArguments().getString(KEY1))!! }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Setup
        viewModel.transaction.onNext(transaction)
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