package com.tminus1010.budgetvalue.choose_transaction_description

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.extensions.easyVisibility
import com.tminus1010.budgetvalue._core.middleware.ui.recipe_factories.itemTextViewRF
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue.databinding.FragTransactionsBinding
import com.tminus1010.budgetvalue.replay_or_future.CreateFutureVM
import com.tminus1010.budgetvalue.transactions.TransactionsVM
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseTransactionDescriptionFrag : Fragment(R.layout.frag_transactions) {
    private val vb by viewBinding(FragTransactionsBinding::bind)
    private val transactionsVM by activityViewModels<TransactionsVM>()
    private val createFutureVM by navGraphViewModels<CreateFutureVM>(R.id.createFutureNestedGraph) { defaultViewModelProviderFactory }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb.tvNoTransactionHistory.easyVisibility = transactionsVM.transactions.value!!.isEmpty()
        vb.tmTableView.initialize(
            recipeGrid = (transactionsVM.transactions.value ?: emptyList())
                .distinctBy { it.description }
                .map { transaction ->
                    listOf(
                        itemTextViewRF().create(transaction.description) {
                            createFutureVM.userSetSearchDescription(transaction.description)
                            nav.navigateUp()
                        },
                    )
                },
            shouldFitItemWidthsInsideTable = true,
        )
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.chooseTransactionDescriptionFrag)
        }
    }
}