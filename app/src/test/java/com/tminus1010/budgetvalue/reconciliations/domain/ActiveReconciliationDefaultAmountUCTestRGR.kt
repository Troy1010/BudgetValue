package com.tminus1010.budgetvalue.reconciliations.domain

import com.tminus1010.budgetvalue._core.models.CategoryAmounts
import com.tminus1010.budgetvalue._shared.app_init.AppInitDomain
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class ActiveReconciliationDefaultAmountUCTestRGR {
    @Test
    fun test() {
        // # Given
        val givenAccountsTotal = BigDecimal("500.00")
        val givenHistoryDefaultAmounts = listOf(
            BigDecimal("75"),
            BigDecimal("217"),
            BigDecimal("43"),
            BigDecimal("93"),
            BigDecimal("-110"),
            BigDecimal("-16"),
        )
        val givenActiveReconciliationCAs = CategoryAmounts(
            AppInitDomain.initCategories[0] to BigDecimal("9"),
        )
        // # When
        val actual = ActiveReconciliationDefaultAmountUC.calcActiveReconciliationDefaultAmount(
            accountsTotal = givenAccountsTotal,
            historyTotalAmounts = givenHistoryDefaultAmounts,
            activeReconciliationCAs = givenActiveReconciliationCAs,
        )
        // # Then
        assertEquals(BigDecimal("189.00"), actual)
    }
}