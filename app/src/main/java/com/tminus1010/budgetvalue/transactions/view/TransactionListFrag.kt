package com.tminus1010.budgetvalue.transactions.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.all_features.framework.view.viewBinding
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.databinding.FragTransactionsBinding
import com.tminus1010.budgetvalue.transactions.presentation.TransactionsVM
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TransactionListFrag : Fragment(R.layout.frag_transactions) {
    private val vb by viewBinding(FragTransactionsBinding::bind)
    private val transactionsVM by activityViewModels<TransactionsVM>()

    @Inject
    lateinit var categoryAmountsConverter: CategoryAmountsConverter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Events
        transactionsVM.navToTransaction.observe(viewLifecycleOwner) { TransactionFrag.navTo(nav, it, categoryAmountsConverter) }
        transactionsVM.alertDialog.observe(viewLifecycleOwner) { it.show(requireContext()) }
        // # State
        vb.buttonsview.buttons = transactionsVM.buttons
        vb.tmTableView.bind(transactionsVM.transactionVMItems) {
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
