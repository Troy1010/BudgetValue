package com.tminus1010.budgetvalue.all.domain

import com.tminus1010.budgetvalue.Given
import com.tminus1010.budgetvalue.all.domain.models.Reconciliations
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
        val result = reconciliations.mostRecent
        // # Then
        assertEquals(result, Given.reconciliation1)
    }
}