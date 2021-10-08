package com.tminus1010.budgetvalue.plans.app

import com.tminus1010.budgetvalue._core.all.extensions.isZero
import com.tminus1010.budgetvalue._core.all.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.app.CategoryAmounts
import com.tminus1010.budgetvalue.plans.data.PlansRepo
import com.tminus1010.budgetvalue.transactions.app.interactor.TransactionsInteractor
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class SetActivePlanFromHistoryInteractor @Inject constructor(
    activePlanDomain: ActivePlanDomain,
    transactionsInteractor: TransactionsInteractor,
    private val plansRepo: PlansRepo
) {
    val setActivePlanFromHistory =
        Observable.combineLatest(activePlanDomain.activePlan, transactionsInteractor.transactionBlocks)
        { activePlan, transactionBlocks ->
            val relevantTransactionBlocks = transactionBlocks.filter { it.defaultAmount.isZero }
            val categoryAmounts =
                relevantTransactionBlocks
                    .fold(CategoryAmounts()) { acc, v -> acc.addTogether(v.categoryAmounts) }
                    .mapValues { (_, v) -> (v / relevantTransactionBlocks.size.toBigDecimal()).toString().toMoneyBigDecimal() }
            plansRepo.updatePlan(activePlan.copy(categoryAmounts = categoryAmounts))
        }.flatMapCompletable { it }
}