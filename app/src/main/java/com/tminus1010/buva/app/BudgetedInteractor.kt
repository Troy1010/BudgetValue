package com.tminus1010.buva.app

import com.tminus1010.buva.data.PlansRepo
import com.tminus1010.buva.data.ReconciliationsRepo
import com.tminus1010.buva.domain.Budgeted
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.CategoryAmounts
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetedInteractor @Inject constructor(
    plansRepo: PlansRepo,
    transactionsInteractor: TransactionsInteractor,
    reconciliationsRepo: ReconciliationsRepo,
) {
    private val categoryAmounts =
        combine(reconciliationsRepo.reconciliations, plansRepo.plans, transactionsInteractor.transactionBlocks, ::Triple)
            .sample(1000)
            .map { (reconciliations, plans, transactionBlocks) ->
                sequenceOf<Map<Category, BigDecimal>>()
                    .plus(reconciliations.map { it.categoryAmounts })
                    .plus(plans.map { it.categoryAmounts })
                    .plus(transactionBlocks.map { it.categoryAmounts })
                    .fold(CategoryAmounts()) { acc, map -> acc.addTogether(map) }
            }
            .shareIn(GlobalScope, SharingStarted.WhileSubscribed(), 1)
    private val totalAmount =
        combine(reconciliationsRepo.reconciliations, plansRepo.plans, transactionsInteractor.transactionBlocks)
        { reconciliations, plans, actuals ->
            reconciliations.map { it.total }.sum() + // TODO: Duplication of ActiveReconciliationInteractor
                    plans.map { it.total }.sum() +
                    actuals.map { it.total }.sum()
        }
            .sample(50)
            .shareIn(GlobalScope, SharingStarted.WhileSubscribed(), 1)
    val budgeted =
        combine(categoryAmounts, totalAmount, ::Budgeted)
            .shareIn(GlobalScope, SharingStarted.WhileSubscribed(), 1)
}