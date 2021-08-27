package com.tminus1010.budgetvalue.behavior_testing

import com.tminus1010.budgetvalue._core.models.CategoryAmounts
import com.tminus1010.budgetvalue._shared.app_init.AppInitDomain
import com.tminus1010.budgetvalue.reconciliations.domain.ActiveReconciliationDefaultAmountUC
import io.reactivex.rxjava3.core.Observable
import org.junit.Test
import java.math.BigDecimal

class ActiveReconciliationDefaultAmountUCTestRGR {
    @Test
    fun test() {
        // # Given
        val givenHistoryDefaultAmounts = listOf(
            BigDecimal("75"),
            BigDecimal("217"),
            BigDecimal("43"),
            BigDecimal("93"),
            BigDecimal("-110"),
            BigDecimal("-16"),
        )
        val givenAccountsTotal = BigDecimal("500.00")
        val givenActiveReconciliationCAs = CategoryAmounts(
            AppInitDomain.initCategories[0] to BigDecimal("9"),
        )
        // # When
        val result = ActiveReconciliationDefaultAmountUC(
            historyTotalAmounts = Observable.just(givenHistoryDefaultAmounts),
            accountsTotal = Observable.just(givenAccountsTotal),
            activeReconciliationCAs = Observable.just(givenActiveReconciliationCAs)
        )
        // # Then
        result().take(1).test().await().assertResult(BigDecimal("189.00"))
    }
}