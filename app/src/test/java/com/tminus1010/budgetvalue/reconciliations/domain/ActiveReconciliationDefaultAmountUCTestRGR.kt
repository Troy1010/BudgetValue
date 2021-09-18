package com.tminus1010.budgetvalue.reconciliations.domain

import com.tminus1010.budgetvalue._core.models.CategoryAmounts
import com.tminus1010.budgetvalue._shared.app_init.AppInitDomain
import com.tminus1010.budgetvalue.plans.models.Plan
import com.tminus1010.budgetvalue.reconciliations.models.Reconciliation
import com.tminus1010.budgetvalue.all.domain.TransactionBlock
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class ActiveReconciliationDefaultAmountUCTestRGR {
    @Test
    fun test() {
        // # Given
        val givenAccountsTotal = BigDecimal("500.00")
        val givenPlans = listOf<Plan>(
            mockk { every { totalAmount() } returns BigDecimal("75") },
        )
        val givenReconciliations = listOf<Reconciliation>(
            mockk { every { totalAmount() } returns BigDecimal("217") },
            mockk { every { totalAmount() } returns BigDecimal("43") },
            mockk { every { totalAmount() } returns BigDecimal("93") },
        )
        val givenTransactionBlocks = listOf<TransactionBlock>(
            mockk { every { totalAmount() } returns BigDecimal("-110") },
            mockk { every { totalAmount() } returns BigDecimal("-16") },
        )
        val givenActiveReconciliationCAs = CategoryAmounts(
            AppInitDomain.initCategories[0] to BigDecimal("9"),
        )
        // # When
        val actual = ActiveReconciliationDefaultAmountUC.calcActiveReconciliationDefaultAmount(
            plans = givenPlans,
            reconciliations = givenReconciliations,
            transactionBlocks = givenTransactionBlocks,
            accountsTotal = givenAccountsTotal,
            activeReconciliationCAs = givenActiveReconciliationCAs,
        )
        // # Then
        assertEquals(BigDecimal("189.00"), actual)
    }
}