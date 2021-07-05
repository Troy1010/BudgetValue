package com.tminus1010.budgetvalue.transactions.models

import com.tminus1010.budgetvalue.Givens
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
                Givens.givenCategories2[0] to BigDecimal("2.00"),
                Givens.givenCategories2[1] to BigDecimal("3.33"),
                Givens.givenCategories2[2] to BigDecimal("3.34"),
            ),
            null,
            "someID"
        )
        // # When
        val result = transaction.categorize(Givens.givenCategories2[0])
        // # Then
        assertEquals(mapOf(
            Givens.givenCategories2[0] to BigDecimal("3.33"),
            Givens.givenCategories2[1] to BigDecimal("3.33"),
            Givens.givenCategories2[2] to BigDecimal("3.34"),
        ), result.categoryAmounts)
    }
}