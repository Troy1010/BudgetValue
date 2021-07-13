package com.tminus1010.budgetvalue._core.models

import com.tminus1010.budgetvalue.Givens
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import junit.framework.TestCase
import java.math.BigDecimal

class CategoryAmountFormulasTest : TestCase() {

    fun testDefaultAmount() {
        // # Given
        val categoryAmounts = CategoryAmountFormulas(
            Givens.givenCategories2[0] to AmountFormula.Value(BigDecimal("-16.5")),
            Givens.givenCategories2[1] to AmountFormula.Value(BigDecimal("-2.5")),
            Givens.givenCategories2[2] to AmountFormula.Value(BigDecimal("-30")),
            Givens.givenCategories2[3] to AmountFormula.Value(BigDecimal("-200")),
        )
        // # When
        val result = categoryAmounts.defaultAmount(BigDecimal("-300"))
        // # Then
        assertEquals(BigDecimal("-51"), result)
    }
}