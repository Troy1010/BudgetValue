package com.tminus1010.budgetvalue.all.app.interactors

import com.tminus1010.budgetvalue._core.extensions.isZero
import com.tminus1010.budgetvalue._core.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.models.CategoryAmounts
import com.tminus1010.budgetvalue.plans.data.PlansRepo
import com.tminus1010.budgetvalue.plans.domain.ActivePlanDomain
import com.tminus1010.budgetvalue.transactions.domain.TransactionsAppService
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class SetActivePlanFromHistory @Inject constructor(
    activePlanDomain: ActivePlanDomain,
    transactionsAppService: TransactionsAppService,
    private val plansRepo: PlansRepo
) : Completable() {
    private val x =
        Observable.combineLatest(activePlanDomain.activePlan, transactionsAppService.transactionBlocks)
        { activePlan, transactionBlocks ->
            val relevantTransactionBlocks = transactionBlocks.filter { it.defaultAmount.isZero }
            val categoryAmounts =
                relevantTransactionBlocks
                    .fold(CategoryAmounts()) { acc, v -> acc.addTogether(v.categoryAmounts) }
                    .mapValues { (_, v) -> (v / relevantTransactionBlocks.size.toBigDecimal()).toString().toMoneyBigDecimal() }
            plansRepo.updatePlan(activePlan.copy(categoryAmounts = categoryAmounts))
        }.flatMapCompletable { it }

    override fun subscribeActual(observer: CompletableObserver) = x.subscribe(observer)
}