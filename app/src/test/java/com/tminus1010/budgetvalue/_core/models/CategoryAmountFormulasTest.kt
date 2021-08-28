package com.tminus1010.budgetvalue._core.models

import com.tminus1010.budgetvalue._shared.app_init.AppInitDomain
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class CategoryAmountFormulasTest {

    @Test
    fun testDefaultAmount() {
        // # Given
        val categoryAmounts = CategoryAmountFormulas(
            AppInitDomain.initCategories[0] to AmountFormula.Value(BigDecimal("-16.5")),
            AppInitDomain.initCategories[1] to AmountFormula.Value(BigDecimal("-2.5")),
            AppInitDomain.initCategories[2] to AmountFormula.Value(BigDecimal("-30")),
            AppInitDomain.initCategories[3] to AmountFormula.Value(BigDecimal("-200")),
        )
        // # When
        val actual = categoryAmounts.defaultAmount(BigDecimal("-300"))
        // # Then
        assertEquals(BigDecimal("-51"), actual)
    }

    @Test
    fun fillIntoCategory() {
        // # Given
        val categoryAmounts = CategoryAmountFormulas(
            AppInitDomain.initCategories[0] to AmountFormula.Value(BigDecimal("-16.5")),
            AppInitDomain.initCategories[1] to AmountFormula.Value(BigDecimal("-2.5")),
            AppInitDomain.initCategories[2] to AmountFormula.Value(BigDecimal("-30")),
            AppInitDomain.initCategories[3] to AmountFormula.Value(BigDecimal("-200")),
        )
        // # When
        val actual = categoryAmounts.fillIntoCategory(AppInitDomain.initCategories[0], BigDecimal("-300"))
        // # Then
        assertEquals(
            CategoryAmountFormulas(
                AppInitDomain.initCategories[0] to AmountFormula.Value(BigDecimal("-67.5")),
                AppInitDomain.initCategories[1] to AmountFormula.Value(BigDecimal("-2.5")),
                AppInitDomain.initCategories[2] to AmountFormula.Value(BigDecimal("-30")),
                AppInitDomain.initCategories[3] to AmountFormula.Value(BigDecimal("-200")),
            ),
            actual
        )
    }
}