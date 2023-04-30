package com.tminus1010.buva.ui.review_spend_bar_chart

import android.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.buva.app.BudgetedInteractor
import com.tminus1010.buva.app.TransactionsInteractor
import com.tminus1010.buva.app.UserCategories
import com.tminus1010.buva.data.AccountsRepo
import com.tminus1010.buva.data.ReconciliationsRepo
import com.tminus1010.buva.domain.GuessPastUtil
import com.tminus1010.buva.ui.all_features.view_model_item.LineChartVMItem
import com.tminus1010.tmcommonkotlin.core.extensions.toDisplayStr
import com.tminus1010.tmcommonkotlin.tuple.tuple
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import kotlin.math.absoluteValue

@HiltViewModel
class ReviewLineChartVM @Inject constructor(
    private val transactionsInteractor: TransactionsInteractor,
    private val accountsRepo: AccountsRepo,
    private val reconciliationsRepo: ReconciliationsRepo,
    private val userCategories: UserCategories,
    private val budgetedInteractor: BudgetedInteractor,
) : ViewModel() {
    private val mapLabelToValues =
        combine(userCategories.flow, transactionsInteractor.transactionBlocks)
        { userCategories, transactionBlocks ->
            val savingsCategory = userCategories.find { it.name == "Savings" } // TODO: Should be an easier way to get Settings.
            transactionBlocks
                .sortedBy { it.datePeriod?.startDate }
                .associate {
                    tuple(
                        it.datePeriod!!.startDate.toDisplayStr(),
                        listOfNotNull(
                            tuple(
                                Color.CYAN,
                                GuessPastUtil.guessAccountsTotalInPast(
                                    it.datePeriod.endDate,
                                    accountsRepo.accountsAggregate.first(),
                                    transactionsInteractor.transactionBlocks.first(),
                                    reconciliationsRepo.reconciliations.first(),
                                ).toFloat()
                            ),
                            tuple(
                                Color.RED,
                                it.spendBlock.total.toFloat().absoluteValue
                            ),
                            tuple(
                                Color.GREEN,
                                it.incomeBlock.total.toFloat().absoluteValue
                            ),
                            if (savingsCategory != null)
                                tuple(
                                    Color.MAGENTA,
                                    GuessPastUtil.budgettedAmountInPast(
                                        savingsCategory,
                                        it.datePeriod.endDate,
                                        budgetedInteractor.budgeted.first().categoryAmounts,
                                        transactionsInteractor.transactionBlocks.first(),
                                        reconciliationsRepo.reconciliations.first(),
                                    ).toFloat()
                                )
                            else null,
//                            tuple(
//                                Color.YELLOW,
//                                it.total.toFloat()
//                            ),
                        ),
                    )
                }
        }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    val lineChartVMItem =
        LineChartVMItem(
            mapLabelToValues = mapLabelToValues,
        )
}