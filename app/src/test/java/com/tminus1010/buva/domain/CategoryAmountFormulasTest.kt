package com.tminus1010.buva.domain

import com.tminus1010.buva.app.InitApp
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class CategoryAmountFormulasTest {
    @Test
    fun testDefaultAmount() {
        // # Given
        val categoryAmounts = CategoryAmountFormulas(
            InitApp.initCategories[0] to AmountFormula.Value(BigDecimal("-16.5")),
            InitApp.initCategories[1] to AmountFormula.Value(BigDecimal("-2.5")),
            InitApp.initCategories[2] to AmountFormula.Value(BigDecimal("-30")),
            InitApp.initCategories[3] to AmountFormula.Value(BigDecimal("-200")),
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
            InitApp.initCategories[0] to AmountFormula.Value(BigDecimal("-16.5")),
            InitApp.initCategories[1] to AmountFormula.Value(BigDecimal("-2.5")),
            InitApp.initCategories[2] to AmountFormula.Value(BigDecimal("-30")),
            InitApp.initCategories[3] to AmountFormula.Value(BigDecimal("-200")),
        )
        // # When
        val actual = categoryAmounts.fillIntoCategory(InitApp.initCategories[0], BigDecimal("-300"))
        // # Then
        assertEquals(
            CategoryAmountFormulas(
                InitApp.initCategories[0] to AmountFormula.Value(BigDecimal("-67.5")),
                InitApp.initCategories[1] to AmountFormula.Value(BigDecimal("-2.5")),
                InitApp.initCategories[2] to AmountFormula.Value(BigDecimal("-30")),
                InitApp.initCategories[3] to AmountFormula.Value(BigDecimal("-200")),
            ),
            actual
        )
    }
}