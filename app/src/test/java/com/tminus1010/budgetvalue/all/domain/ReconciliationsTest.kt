package com.tminus1010.budgetvalue.all.domain

import com.tminus1010.budgetvalue.Given
import org.junit.Assert.assertEquals
import org.junit.Test

class ReconciliationsTest {
    @Test
    fun last() {
        // # Given
        val reconciliations = Reconciliations(
            listOf(
                Given.reconciliation1,
                Given.reconciliation2,
            )
        )
        // # When
        val result = reconciliations.last
        // # Then
        assertEquals(result, Given.reconciliation1)
    }
}