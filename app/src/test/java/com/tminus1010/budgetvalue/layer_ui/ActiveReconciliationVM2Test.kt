package com.tminus1010.budgetvalue.layer_ui

import com.tminus1010.budgetvalue.Givens
import com.tminus1010.budgetvalue.extensions.toObservable
import com.tminus1010.budgetvalue.features_shared.Domain
import com.tminus1010.budgetvalue.features_shared.budgeted.BudgetedVM
import com.tminus1010.budgetvalue.features.reconciliations.ActiveReconciliationVM2
import com.tminus1010.budgetvalue.features.transactions.TransactionsVM
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
        domain = mockk<Domain>().also {
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