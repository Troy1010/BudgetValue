package com.tminus1010.buva.app

import com.tminus1010.buva.data.AccountsRepo
import com.tminus1010.buva.data.ActivePlanRepo
import com.tminus1010.buva.data.ActiveReconciliationRepo
import com.tminus1010.buva.data.ReconciliationsRepo
import com.tminus1010.buva.domain.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

class ActiveReconciliationInteractor @Inject constructor(
    private val activeReconciliationRepo: ActiveReconciliationRepo,
    private val reconciliationsToDoInteractor: ReconciliationsToDoInteractor,
    private val activePlanRepo: ActivePlanRepo,
    private val reconciliationsRepo: ReconciliationsRepo,
    private val planReconciliationInteractor: PlanReconciliationInteractor,
    accountsRepo: AccountsRepo,
    transactionsInteractor: TransactionsInteractor,
) {
    suspend fun fillIntoCategory(category: Category) {
        val activeReconciliationCAs = activeReconciliationRepo.activeReconciliationCAs.first()
        val targetDefaultAmount = targetDefaultAmount.first()
        activeReconciliationRepo.pushCategoryAmount(
            category = category,
            amount = activeReconciliationCAs.calcCategoryAmountToGetTargetDefaultAmount(category, targetDefaultAmount),
        )
    }

    suspend fun save() {
        when (val reconciliationToDo = reconciliationsToDoInteractor.currentReconciliationToDo.first()) {
            is ReconciliationToDo.PlanZ ->
                planReconciliationInteractor.save()
            else ->
                reconciliationsRepo.push(
                    Reconciliation(
                        date = when (reconciliationToDo) {
                            is ReconciliationToDo.Anytime ->
                                LocalDate.now()
                            is ReconciliationToDo.Accounts ->
                                reconciliationToDo.date
                            else -> error("Unhandled type:$reconciliationToDo")
                        },
                        total = activeReconciliationCAsAndTotal.first().total,
                        categoryAmounts = activeReconciliationRepo.activeReconciliationCAs.first(),
                    )
                )
        }
    }

    suspend fun reset() {
        when (reconciliationsToDoInteractor.currentReconciliationToDo.first()) {
            is ReconciliationToDo.PlanZ ->
                planReconciliationInteractor.reset()
//                    activePlanRepo.activePlan.first().categoryAmounts
//                    CategoryAmounts()
            else ->
                activeReconciliationRepo.pushCategoryAmounts(CategoryAmounts())
        }
    }

    val activeReconciliationCAsAndTotal =
        reconciliationsToDoInteractor.currentReconciliationToDo.flatMapLatest { currentReconciliationToDo ->
            when (currentReconciliationToDo) {
                is ReconciliationToDo.PlanZ ->
                    planReconciliationInteractor.activeReconciliationCAsAndTotal
                else ->
                    combine(activeReconciliationRepo.activeReconciliationCAs, accountsRepo.accountsAggregate, transactionsInteractor.transactionBlocks, reconciliationsRepo.reconciliations)
                    { activeReconciliationCAs, accountsAggregate, transactionBlocks, reconciliations ->
                        CategoryAmountsAndTotal.FromTotal(
                            categoryAmounts = activeReconciliationCAs,
                            total = when (currentReconciliationToDo) {
                                is ReconciliationToDo.Accounts ->
                                    Domain.guessAccountsTotalInPast(currentReconciliationToDo.date, accountsAggregate, transactionBlocks, reconciliations)
                                else -> BigDecimal.ZERO
                            },
                        )
                    }
            }
        }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    val targetDefaultAmount =
        reconciliationsToDoInteractor.currentReconciliationToDo.flatMapLatest { currentReconciliationToDo ->
            when (currentReconciliationToDo) {
                is ReconciliationToDo.PlanZ ->
                    planReconciliationInteractor.targetDefaultAmount
                else ->
                    flowOf(BigDecimal.ZERO)
            }
        }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    init {
        // Requirement: Reset ActiveReconciliation whenever something it derives from changes.
        //      A reset should not occur when reconciliationsToDoInteractor.currentReconciliationToDo first emits, as it always does at the start.
        //      A reset should always occur if currentReconciliation is a plan and activePlan emits.
        GlobalScope.launch {
            merge(
                reconciliationsToDoInteractor.currentReconciliationToDo.take(1)
                    .flatMapLatest {
                        when (it) {
                            is ReconciliationToDo.PlanZ ->
                                activePlanRepo.activePlan.drop(1) // TODO: This is not disposing when currentReconciliationToDo.drop(1) emits..
                            else ->
                                flowOf()
                        }
                    },
                reconciliationsToDoInteractor.currentReconciliationToDo.drop(1)
                    .flatMapLatest {
                        when (it) {
                            is ReconciliationToDo.PlanZ ->
                                activePlanRepo.activePlan
                            else ->
                                flowOf(Unit)
                        }
                    },
            )
                .debounce(1000) // TODO: This is not ideal.
                .collect { reset() }
        }
    }
}