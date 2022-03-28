package com.tminus1010.budgetvalue._unrestructured.transactions.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.framework.android.viewBinding
import com.tminus1010.budgetvalue._unrestructured.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.databinding.FragTransactionsBinding
import com.tminus1010.budgetvalue._unrestructured.transactions.presentation.TransactionsVM
import com.tminus1010.budgetvalue.all_layers.extensions.onNext
import com.tminus1010.budgetvalue.all_layers.extensions.showAlertDialog
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TransactionListFrag : Fragment(R.layout.frag_transactions) {
    private val vb by viewBinding(FragTransactionsBinding::bind)
    private val viewModel by activityViewModels<TransactionsVM>()

    @Inject
    lateinit var categoryAmountsConverter: CategoryAmountsConverter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Setup
        viewModel.showAlertDialog.onNext(showAlertDialog)
        // # Events
        viewModel.navToTransaction.observe(viewLifecycleOwner) { TransactionFrag.navTo(nav, it, categoryAmountsConverter) }
        // # State
        vb.buttonsview.buttons = viewModel.buttons
        vb.tmTableView.bind(viewModel.transactionVMItems) {
            initialize(
                recipeGrid = it.map { it.toViewItemRecipes(requireContext()) },
                shouldFitItemWidthsInsideTable = true
            )
        }
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.transactionsFrag)
        }
    }
}
