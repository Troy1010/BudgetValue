package com.tminus1010.budgetvalue.all.app.interactor

import com.tminus1010.budgetvalue._core.domain.LocalDatePeriod
import com.tminus1010.budgetvalue._shared.app_init.AppInit
import com.tminus1010.budgetvalue.all.domain.models.ReconciliationToDo
import com.tminus1010.budgetvalue.all.domain.models.TransactionBlock
import com.tminus1010.budgetvalue.plans.models.Plan
import com.tminus1010.budgetvalue.transactions.models.Transaction
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

// TODO()
class ReconciliationsToDoInteractor @Inject constructor(
) {
    val reconciliationsToDo =
        Observable.just(
            listOf(
                ReconciliationToDo.PlanZ(
                    Plan(
                        LocalDatePeriod(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 2, 1)),
                        BigDecimal("800"),
                        mapOf(
                            AppInit.initCategories[0] to BigDecimal("40"),
                            AppInit.initCategories[1] to BigDecimal("24"),
                            AppInit.initCategories[2] to BigDecimal("41"),
                        )
                    ),
                    TransactionBlock(
                        listOf(
                            Transaction(
                                LocalDate.of(2020, 1, 5),
                                "Zoop",
                                BigDecimal("70"),
                                mapOf(
                                    AppInit.initCategories[0] to BigDecimal("35"),
                                    AppInit.initCategories[1] to BigDecimal("35"),
                                ),
                                LocalDate.of(2020, 3, 1),
                                "156835"
                            )
                        ),
                        LocalDatePeriod(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 2, 1))
                    )
                ),
                ReconciliationToDo.Accounts,
                ReconciliationToDo.Anytime,
            )
        )
}