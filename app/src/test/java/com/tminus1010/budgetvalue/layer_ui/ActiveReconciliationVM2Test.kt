package com.tminus1010.budgetvalue.layer_ui

import com.tminus1010.budgetvalue.Givens
import com.tminus1010.budgetvalue._core.extensions.toObservable
import com.tminus1010.budgetvalue.features_shared.Domain
import com.tminus1010.budgetvalue.budgeted.BudgetedVM
import com.tminus1010.budgetvalue.reconciliations.ActiveReconciliationVM2
import com.tminus1010.budgetvalue.transactions.TransactionsVM
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase

class ActiveReconciliationVM2Test : TestCase() {

    val activeReconciliationVM2 = ActiveReconciliationVM2(
        activeReconciliationVM = mockk(),
        budgetedVM = mockk<BudgetedVM>().also {
            every { it.defaultAmount } returns 298.toBigDecimal().toObservable().toBehaviorSubject()
        },
        domainFacade = mockk<Domain>().also {
            every { it.plans } returns Givens.givenPlans
            every { it.reconciliations } returns Givens.givenReconciliations
        },
        transactionsVM = mockk<TransactionsVM>().also {
            every { it.transactionBlocks } returns Givens.givenTransactionBlocks
        }
    )

    fun testGetDefaultAmount() {
        activeReconciliationVM2.defaultAmount.take(1).test().apply {
            assertResult(189.toBigDecimal())
        }
    }
}