package com.tminus1010.budgetvalue.layer_ui

import com.tminus1010.budgetvalue.Givens
import com.tminus1010.budgetvalue.layer_domain.Domain
import com.tminus1010.budgetvalue.model_domain.Category
import com.tminus1010.budgetvalue.model_domain.LocalDatePeriod
import com.tminus1010.budgetvalue.model_domain.Plan
import com.tminus1010.budgetvalue.source_objects.SourceHashMap
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import junit.framework.TestCase

class BudgetedVMTest : TestCase() {
    val budgetedVM = BudgetedVM(
        domain = mockk<Domain>().also {
            every { it.userCategories } returns Givens.givenCategories
            every { it.plans } returns Givens.givenPlans
            every { it.reconciliations } returns Givens.givenReconciliations
        },
        transactionsVM = mockk<TransactionsVM>().also {
            every { it.transactionBlocks } returns Givens.givenTransactionBlocks
        },
        activeReconciliationVM = mockk<ActiveReconciliationVM>().also {
            every { it.activeReconcileCAs } returns Givens.givenActiveReconcileCAs
        },
        accountsVM = mockk<AccountsVM>().also {
            every { it.accountsTotal } returns BehaviorSubject.createDefault(500.toBigDecimal())
        }
    )

    fun testGetCategoryAmounts() {
        budgetedVM.categoryAmounts.take(1).test().apply {
            assertResult(SourceHashMap(
                null,
                Givens.givenCategories.value[0] to 40.toBigDecimal()
            ))
        }
    }

    fun testGetCaTotal() {
        budgetedVM.caTotal.take(1).test().apply {
            assertResult(60.toBigDecimal())
        }
    }

    fun testGetDefaultAmount() {}
}