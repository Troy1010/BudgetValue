package com.tminus1010.budgetvalue.choose_transaction_description

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.extensions.bind
import com.tminus1010.budgetvalue._core.extensions.easyVisibility
import com.tminus1010.budgetvalue._core.middleware.ui.recipe_factories.itemTextViewRB
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue.databinding.FragTransactionsBinding
import com.tminus1010.budgetvalue.transactions.ReplayVM
import com.tminus1010.budgetvalue.transactions.TransactionsVM
import com.tminus1010.tmcommonkotlin.rx.extensions.doLogx
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseTransactionDescriptionFrag2 : Fragment(R.layout.frag_transactions) {
    private val vb by viewBinding(FragTransactionsBinding::bind)
    private val transactionsVM by activityViewModels<TransactionsVM>()
    private val replayVM by activityViewModels<ReplayVM>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb.tvNoTransactionHistory.bind(transactionsVM.transactions.doLogx("aaa")) { easyVisibility = it.isEmpty() }
        vb.tmTableView.bind(transactionsVM.transactions) { transactions ->
            initialize(
                recipeGrid = transactions
                    .distinctBy { it.description }
                    .map { transaction ->
                        val shouldDisable = transaction.description in replayVM.searchTexts.value!!
                        listOf(
                            itemTextViewRB().create(transaction.description, requireContext(), shouldDisable) {
                                if (!shouldDisable) {
                                    replayVM.userAddSearchText(transaction.description)
                                    nav.navigateUp()
                                }
                            },
                        )
                    },
                shouldFitItemWidthsInsideTable = true,
            )
        }
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.chooseTransactionDescriptionFrag)
        }
    }
}