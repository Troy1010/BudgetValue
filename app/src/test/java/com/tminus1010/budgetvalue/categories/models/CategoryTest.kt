package com.tminus1010.budgetvalue.categories.models

import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import junit.framework.TestCase
import java.math.BigDecimal

class CategoryTest : TestCase() {

    fun testToDTOFromDTO() {
        // # Given
        val category = Category("Some Category", defaultAmountFormula = AmountFormula.Value(BigDecimal("-100")))
        // # When
        val result = category.toDTO().let { Category.fromDTO(it) }
        // # Then
        assertEquals(category, result)
    }
}