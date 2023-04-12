package com.tminus1010.buva.app

import com.tminus1010.buva.all_layers.InvalidStateException
import com.tminus1010.buva.data.ActiveReconciliationRepo
import com.tminus1010.buva.data.ReconciliationsRepo
import com.tminus1010.buva.domain.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // TODO: Using Singleton to avoid excessive leaks while using GlobalScope without any disposal strategy.
class ActivePlanReconciliationInteractor @Inject constructor(
    private val activeReconciliationRepo: ActiveReconciliationRepo,
    private val reconciliationsToDoInteractor: ReconciliationsToDoInteractor,
    private val historyInteractor: HistoryInteractor,
    private val reconciliationsRepo: ReconciliationsRepo,
    private val activePlanInteractor: ActivePlanInteractor,
) {
    suspend fun fillIntoCategory(category: Category) {
        val activeReconciliationCAs = activeReconciliationRepo.activeReconciliationCAs.first()
        val targetDefaultAmount = activeReconciliationCAsAndTotal.first().defaultAmount - budgeted.first().defaultAmount
        activeReconciliationRepo.pushCategoryAmount(
            category = category,
            amount = activeReconciliationCAs.calcCategoryAmountToGetTargetDefaultAmount(category, targetDefaultAmount),
        )
    }

    suspend fun save() {
        if (!budgeted.first().isAllValid) throw InvalidStateException()
        val reconciliationToDo = reconciliationsToDoInteractor.currentReconciliationToDo.first() as ReconciliationToDo.PlanZ
        val casAndTotalToPush =
            CategoryAmountsAndTotal.addTogether(
                activeReconciliationCAsAndTotal.first(),
                activePlanInteractor.activePlan.first()
            )
        reconciliationsRepo.push(
            Reconciliation(
                date = reconciliationToDo.transactionBlock.datePeriod!!.midDate,
                total = casAndTotalToPush.total,
                categoryAmounts = casAndTotalToPush.categoryAmounts,
            )
        )
    }

    suspend fun resolve() {
        val activeReconciliationCAs = activeReconciliationRepo.activeReconciliationCAs.first()
        val activePlanCAs = activePlanInteractor.activePlan.first().categoryAmounts
        val budgetedCAs = budgeted.first().categoryAmounts
        val categories = (activeReconciliationCAs.keys + budgetedCAs.keys)
        activeReconciliationRepo.pushCategoryAmounts(
            categories
                .associateWith {
                    when (val x = it.reconciliationStrategyGroup.planResolutionStrategy) {
                        is ResolutionStrategy.Basic -> x.calc(it, activeReconciliationCAs, budgetedCAs)
                        is ResolutionStrategy.MatchPlan -> x.calc(it, activeReconciliationCAs, budgetedCAs, activePlanCAs)
                    }
                }
                .toCategoryAmounts()
        )
    }

    suspend fun reset() {
        val activeReconciliationCAs = activeReconciliationRepo.activeReconciliationCAs.first()
        val budgetedCAs = budgeted.first().categoryAmounts
        val categories = (activeReconciliationCAs.keys + budgetedCAs.keys)
        activeReconciliationRepo.pushCategoryAmounts(
            categories
                .associateWith {
                    when (val x = it.reconciliationStrategyGroup.resetStrategy) {
                        is ResetStrategy.Basic -> x.calc(it, activeReconciliationCAs, budgetedCAs)
                        else -> activeReconciliationCAs[it] ?: BigDecimal.ZERO
                    }
                }
                .toCategoryAmounts()
        )
    }

    // # Private
    private val summedRelevantHistory =
        combine(reconciliationsToDoInteractor.currentReconciliationToDo.filterIsInstance<ReconciliationToDo.PlanZ>(), historyInteractor.entireHistory)
        { currentReconciliationToDo, entireHistory ->
            entireHistory.filter {
                when (it) {
                    is Reconciliation -> it.date < currentReconciliationToDo.transactionBlock.datePeriod!!.endDate
                    is TransactionBlock -> it.datePeriod!!.startDate < currentReconciliationToDo.transactionBlock.datePeriod!!.endDate
                    else -> true
                }
            }
                .addTogether()
        }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    //

    // TODO: This is a quasi-redefinition of ActiveReconciliationInteractor.categoryAmountsAndTotal
    val activeReconciliationCAsAndTotal =
        activeReconciliationRepo.activeReconciliationCAs.map { activeReconciliationCAs ->
            CategoryAmountsAndTotal.FromTotal(
                categoryAmounts = activeReconciliationCAs,
                total = BigDecimal.ZERO,
            )
        }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    val budgeted =
        combine(activeReconciliationCAsAndTotal, summedRelevantHistory, activePlanInteractor.activePlan)
        { activeReconciliation, summedRelevantHistory, activePlan ->
            CategoryAmountsAndTotalWithValidation(
                categoryAmountsAndTotal = CategoryAmountsAndTotal.addTogether(
                    activeReconciliation,
                    summedRelevantHistory,
                    CategoryAmountsAndTotal.FromTotal(activePlan.categoryAmounts, BigDecimal.ZERO),
                ),
                caValidation = { (it ?: BigDecimal.ZERO) >= BigDecimal.ZERO }, // TODO: Doesn't really make sense to have caValidation when each category is validated in its own way.
                defaultAmountValidation = { true },
            )
        }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    val targetDefaultAmount =
        combine(budgeted, activeReconciliationCAsAndTotal)
        { budgeted, activeReconciliationCAsAndTotal ->
            activeReconciliationCAsAndTotal.defaultAmount - budgeted.defaultAmount
        }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)
}
