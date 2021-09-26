package com.tminus1010.budgetvalue.choose_transaction_description

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.extensions.bind
import com.tminus1010.budgetvalue._core.extensions.easyVisibility
import com.tminus1010.budgetvalue._core.middleware.view.recipe_factories.itemTextViewRB
import com.tminus1010.budgetvalue._core.middleware.view.viewBinding
import com.tminus1010.budgetvalue.databinding.FragTransactionsBinding
import com.tminus1010.budgetvalue.replay_or_future.CreateFutureVM
import com.tminus1010.budgetvalue.transactions.domain.TransactionsAppService
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

@AndroidEntryPoint
class ChooseTransactionDescriptionFrag : Fragment(R.layout.frag_transactions) {
    private val vb by viewBinding(FragTransactionsBinding::bind)
    @Inject lateinit var transactionsAppService: TransactionsAppService
    private val createFutureVM by navGraphViewModels<CreateFutureVM>(R.id.categorizeNestedGraph) { defaultViewModelProviderFactory }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb.tvNoTransactionHistory.bind(transactionsAppService.transactions) { easyVisibility = it.isEmpty() }
        // TODO: This should be moved into a VM. I have not done so yet b/c I need to figure out how to not have 2 ChooseTransactionDescriptionFrag first.
        val _transactions =
            Observable.combineLatest(transactionsAppService.transactions, transactionsAppService.mostRecentUncategorizedSpend)
            { transactions, (firstUncategorizedSpend) ->
                transactions
                    .run { if (firstUncategorizedSpend == null) this else listOf(firstUncategorizedSpend) + this }
                    .distinctBy { it.description }
            }
        vb.tmTableView.bind(_transactions) { transactions ->
            initialize(
                recipeGrid = transactions
                    .map { transaction ->
                        listOf(
                            itemTextViewRB().create(transaction.description) {
                                createFutureVM.userSetSearchDescription(transaction.description)
                                nav.navigateUp()
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