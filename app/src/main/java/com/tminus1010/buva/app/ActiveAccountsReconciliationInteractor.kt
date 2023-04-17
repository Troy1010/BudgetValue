package com.tminus1010.buva.app

import com.tminus1010.buva.all_layers.InvalidStateException
import com.tminus1010.buva.all_layers.extensions.isZero
import com.tminus1010.buva.data.AccountsRepo
import com.tminus1010.buva.data.ActivePlanRepo
import com.tminus1010.buva.data.ActiveReconciliationRepo
import com.tminus1010.buva.data.ReconciliationsRepo
import com.tminus1010.buva.domain.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // TODO: Using Singleton to avoid excessive leaks while using GlobalScope without any disposal strategy.
class ActiveAccountsReconciliationInteractor @Inject constructor(
    private val activeReconciliationRepo: ActiveReconciliationRepo,
    private val reconciliationsToDoInteractor: ReconciliationsToDoInteractor,
    private val activePlanRepo: ActivePlanRepo,
    private val reconciliationsRepo: ReconciliationsRepo,
    private val accountsRepo: AccountsRepo,
    private val transactionsInteractor: TransactionsInteractor,
    private val historyInteractor: HistoryInteractor,
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
        reconciliationsRepo.push(
            Reconciliation(
                date = when (val reconciliationToDo = reconciliationsToDoInteractor.currentReconciliationToDo.first()) {
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

    suspend fun resolve() {
        val activeReconciliationCAs = activeReconciliationRepo.activeReconciliationCAs.first()
        val budgetedCAs = budgeted.first().categoryAmounts
        val activePlanCAs = activePlanRepo.activePlan.first().categoryAmounts
        val categories = (activeReconciliationCAs.keys + budgetedCAs.keys + activePlanCAs.keys)
        activeReconciliationRepo.pushCategoryAmounts(
            categories
                .associateWith {
                    when (val x = it.reconciliationStrategyGroup.anytimeResolutionStrategy) {
                        is ResolutionStrategy.Basic -> x.calc(it, budgetedCAs[it], activeReconciliationCAs)
                        is ResolutionStrategy.MatchPlan -> x.calc(it, budgetedCAs[it], activeReconciliationCAs, activePlanCAs)
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
                        null -> activeReconciliationCAs[it] ?: BigDecimal.ZERO
                    }
                }
                .toCategoryAmounts()
        )
    }

    val activeReconciliationCAsAndTotal =
        reconciliationsToDoInteractor.currentReconciliationToDo.flatMapLatest { currentReconciliationToDo ->
            when (currentReconciliationToDo) {
                is ReconciliationToDo.PlanZ ->
                    flowOf()
                else ->
                    combine(activeReconciliationRepo.activeReconciliationCAs, accountsRepo.accountsAggregate, transactionsInteractor.transactionBlocks, reconciliationsRepo.reconciliations)
                    { activeReconciliationCAs, accountsAggregate, transactionBlocks, reconciliations ->
                        CategoryAmountsAndTotal.FromTotal(
                            categoryAmounts = activeReconciliationCAs,
                            total = when (currentReconciliationToDo) {
                                is ReconciliationToDo.Accounts ->
                                    MiscUtil.guessAccountsTotalInPast(currentReconciliationToDo.date, accountsAggregate, transactionBlocks, reconciliations)
                                else -> BigDecimal.ZERO
                            },
                        )
                    }
            }
        }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    val budgeted =
        combine(activeReconciliationCAsAndTotal, historyInteractor.entireHistory)
        { activeReconciliationCAsAndTotal, entireHistory ->
            CategoryAmountsAndTotalWithValidation(
                categoryAmountsAndTotal = CategoryAmountsAndTotal.addTogether(
                    entireHistory.addedTogether,
                    activeReconciliationCAsAndTotal,
                ),
                caValidation = { category, amount ->
                    when (val x = category.reconciliationStrategyGroup.anytimeResolutionStrategy) {
                        is ResolutionStrategy.MatchPlan -> error("MatchPlan doesn't really work here")
                        is ResolutionStrategy.Basic -> x.validation(category, amount)
                    }
                },
                defaultAmountValidation = { if (it?.isZero ?: true) ValidationResult.Success else ValidationResult.Warning },
            )
        }
            .shareIn(GlobalScope, SharingStarted.Eagerly, 1)
}