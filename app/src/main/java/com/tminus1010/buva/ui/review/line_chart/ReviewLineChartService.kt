package com.tminus1010.buva.ui.review.line_chart

import android.graphics.Color
import com.tminus1010.buva.all_layers.extensions.throttleFist
import com.tminus1010.buva.app.BudgetedInteractor
import com.tminus1010.buva.app.TransactionsInteractor
import com.tminus1010.buva.app.UserCategories
import com.tminus1010.buva.data.AccountsRepo
import com.tminus1010.buva.data.CurrentDate
import com.tminus1010.buva.data.ReconciliationsRepo
import com.tminus1010.buva.domain.GuessPastUtil
import com.tminus1010.tmcommonkotlin.core.extensions.toDisplayStr
import com.tminus1010.tmcommonkotlin.tuple.tuple
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.absoluteValue

// TODO: Is this a good name? Is it packaged correctly?
@Singleton
class ReviewLineChartService @Inject constructor(
    private val transactionsInteractor: TransactionsInteractor,
    private val accountsRepo: AccountsRepo,
    private val reconciliationsRepo: ReconciliationsRepo,
    private val userCategories: UserCategories,
    private val budgetedInteractor: BudgetedInteractor,
    private val currentDate: CurrentDate,
) {
    // TODO: This can be heavily optimized.
    val mapLabelToValues =
        combine(userCategories.flow, transactionsInteractor.transactionBlocks, currentDate.flow, ::tuple)
            .debounce(2000)
            .map { (userCategories, transactionBlocks, currentDate) ->
                val savingsCategory = userCategories.find { it.name == "Savings" } // TODO: Should be an easier way to get Settings.
                transactionBlocks
                    .filter { currentDate !in it.datePeriod!! }
                    .sortedBy { it.datePeriod?.startDate }
                    .drop(1)
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
                                    ).toFloat(),
                                ),
                                tuple(
                                    Color.RED,
                                    it.spendBlock.total.toFloat().absoluteValue,
                                ),
                                tuple(
                                    Color.GREEN,
                                    it.incomeBlock.total.toFloat().absoluteValue,
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
                                        ).toFloat(),
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
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)
}