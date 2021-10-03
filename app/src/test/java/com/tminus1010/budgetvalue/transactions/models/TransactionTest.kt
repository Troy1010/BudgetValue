package com.tminus1010.budgetvalue.transactions.models

import com.tminus1010.budgetvalue.Given
import com.tminus1010.budgetvalue.transactions.app.Transaction
import io.mockk.mockk
import junit.framework.TestCase
import java.math.BigDecimal

class TransactionTest : TestCase() {
    fun testCategorize() {
        // # Given
        val transaction = Transaction(
            mockk(),
            description = "description",
            amount = BigDecimal.TEN,
            categoryAmounts = mapOf(
                Given.categories[0] to BigDecimal("2.00"),
                Given.categories[1] to BigDecimal("3.33"),
                Given.categories[2] to BigDecimal("3.34"),
            ),
            null,
            "someID"
        )
        // # When
        val result = transaction.categorize(Given.categories[0])
        // # Then
        assertEquals(mapOf(
            Given.categories[0] to BigDecimal("3.33"),
            Given.categories[1] to BigDecimal("3.33"),
            Given.categories[2] to BigDecimal("3.34"),
        ), result.categoryAmounts)
    }
}