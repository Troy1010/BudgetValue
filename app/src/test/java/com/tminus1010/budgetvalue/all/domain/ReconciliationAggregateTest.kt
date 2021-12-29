package com.tminus1010.budgetvalue.all.domain

import com.tminus1010.budgetvalue.Given
import com.tminus1010.budgetvalue.reconcile.domain.ReconciliationAggregate
import org.junit.Assert.assertEquals
import org.junit.Test

class ReconciliationAggregateTest {
    @Test
    fun last() {
        // # Given
        val reconciliations = ReconciliationAggregate(
            listOf(
                Given.reconciliation1,
                Given.reconciliation2,
            )
        )
        // # When
        val result = reconciliations.mostRecent
        // # Then
        assertEquals(result, Given.reconciliation1)
    }
}