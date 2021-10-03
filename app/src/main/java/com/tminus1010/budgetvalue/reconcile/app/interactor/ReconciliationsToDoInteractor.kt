package com.tminus1010.budgetvalue.reconcile.app.interactor

import com.tminus1010.budgetvalue.all.domain.models.ReconciliationToDo
import com.tminus1010.budgetvalue.plans.data.PlansRepo
import com.tminus1010.budgetvalue.reconcile.data.ReconciliationsRepo
import com.tminus1010.budgetvalue.transactions.app.TransactionsInteractor
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

// TODO()
class ReconciliationsToDoInteractor @Inject constructor(
    plansRepo: PlansRepo,
    transactionsInteractor: TransactionsInteractor,
    reconciliationsRepo: ReconciliationsRepo,
) {
    val planReconciliationsToDo =
        Observable.combineLatest(plansRepo.plans, transactionsInteractor.transactionBlocks, reconciliationsRepo.reconciliations)
        { plans, transactionBlocks, reconciliations ->
            plans.map { plan ->
                Triple(
                    plan,
                    transactionBlocks.find { it.datePeriod != null && plan.localDatePeriod == it.datePeriod },
                    reconciliations.find { it.localDate in plan.localDatePeriod }
                )
            }
                .filter { (plan, transactionBlock, reconciliation) ->
                    transactionBlock != null
                            && transactionBlock.isFullyImported
                            && transactionBlock.isFullyCategorized
                            && reconciliation == null
                }
                .map { ReconciliationToDo.PlanZ(it.first, it.second!!) }
        }

    val reconciliationsToDo =
        planReconciliationsToDo
//        Observable.just(
//            listOf(
//                ReconciliationToDo.PlanZ(
//                    Plan(
//                        LocalDatePeriod(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 2, 1)),
//                        BigDecimal("800"),
//                        mapOf(
//                            AppInit.initCategories[0] to BigDecimal("40"),
//                            AppInit.initCategories[1] to BigDecimal("24"),
//                            AppInit.initCategories[2] to BigDecimal("41"),
//                        )
//                    ),
//                    TransactionBlock(
//                        listOf(
//                            Transaction(
//                                LocalDate.of(2020, 1, 5),
//                                "Zoop",
//                                BigDecimal("70"),
//                                mapOf(
//                                    AppInit.initCategories[0] to BigDecimal("35"),
//                                    AppInit.initCategories[1] to BigDecimal("35"),
//                                ),
//                                LocalDate.of(2020, 3, 1),
//                                "156835"
//                            )
//                        ),
//                        LocalDatePeriod(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 2, 1))
//                    )
//                ),
//                ReconciliationToDo.Accounts,
//                ReconciliationToDo.Anytime,
//            )
//        )
}