package com.tminus1010.budgetvalue.choose_transaction_description

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.all.extensions.bind
import com.tminus1010.budgetvalue._core.all.extensions.mapBox
import com.tminus1010.budgetvalue._core.framework.view.recipe_factories.itemEditTextRF
import com.tminus1010.budgetvalue._core.framework.view.recipe_factories.itemTextViewRB
import com.tminus1010.budgetvalue._core.framework.view.viewBinding
import com.tminus1010.budgetvalue.databinding.FragChooseTransactionDesciption2Binding
import com.tminus1010.budgetvalue.transactions.app.TransactionsAggregate
import com.tminus1010.budgetvalue.transactions.data.repo.TransactionsRepo
import com.tminus1010.budgetvalue.transactions.presentation.ReplayVM
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.view.extensions.easyToast
import com.tminus1010.tmcommonkotlin.view.extensions.easyVisibility
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject


// TODO: ChooseTransactionDescriptionFrag outputs to createFutureVM, while ChooseTransactionDescriptionFrag2
//      outputs to replayVM. There's probably an easier way to do this..
@AndroidEntryPoint
class ChooseTransactionDescriptionFrag2 : Fragment(R.layout.frag_choose_transaction_desciption_2) {
    private val vb by viewBinding(FragChooseTransactionDesciption2Binding::bind)
    private val replayVM by activityViewModels<ReplayVM>()

    @Inject
    lateinit var transactionsRepo: TransactionsRepo
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb.tvNoTransactionHistory.bind(transactionsRepo.transactionsAggregate.map(TransactionsAggregate::transactions)) { easyVisibility = it.isEmpty() }
        // TODO: This should be moved into a VM. I have not done so yet b/c I need to figure out how to not have 2 ChooseTransactionDescriptionFrag first.
        val _transactions =
            Observable.combineLatest(transactionsRepo.transactionsAggregate.map(TransactionsAggregate::transactions), transactionsRepo.transactionsAggregate.mapBox(TransactionsAggregate::mostRecentUncategorizedSpend))
            { transactions, (firstUncategorizedSpend) ->
                transactions
                    .run { if (firstUncategorizedSpend == null) this else listOf(firstUncategorizedSpend) + this }
                    .distinctBy { it.description }
            }
        var searchText = ""
        vb.tmTableViewTop.initialize(
            recipeGrid = listOf(
                listOf(
                    itemTextViewRB().create("SearchText") {
                        if (searchText == "")
                            easyToast("SearchText was empty")
                        else {
                            replayVM.userAddSearchText(searchText)
                            nav.navigateUp()
                        }
                    },
                    itemEditTextRF().create { searchText = it }
                )
            ),
            shouldFitItemWidthsInsideTable = true,
        )
        vb.tmTableView.bind(_transactions) { transactions ->
            initialize(
                recipeGrid = transactions
                    .map { transaction ->
                        // TODO: This shouldDisable derivative value indicates that we should probably make a transactionVMItem.
                        val shouldDisable = transaction.description in replayVM.searchTexts.value!!
                        listOf(
                            itemTextViewRB().create(transaction.description, requireContext(), shouldDisable) {
                                if (!shouldDisable) {
                                    replayVM.userAddSearchText(transaction.description)
                                    nav.navigateUp()
                                } else
                                    easyToast("Replay already has this searchText")
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
