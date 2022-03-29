package com.tminus1010.budgetvalue.all_features.models

import com.tminus1010.budgetvalue.domain.CategoryAmountFormulas
import com.tminus1010.budgetvalue.app.TryInitApp
import com.tminus1010.budgetvalue.domain.AmountFormula
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class CategoryAmountFormulasTest {
    @Test
    fun testDefaultAmount() {
        // # Given
        val categoryAmounts = CategoryAmountFormulas(
            TryInitApp.initCategories[0] to AmountFormula.Value(BigDecimal("-16.5")),
            TryInitApp.initCategories[1] to AmountFormula.Value(BigDecimal("-2.5")),
            TryInitApp.initCategories[2] to AmountFormula.Value(BigDecimal("-30")),
            TryInitApp.initCategories[3] to AmountFormula.Value(BigDecimal("-200")),
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
            TryInitApp.initCategories[0] to AmountFormula.Value(BigDecimal("-16.5")),
            TryInitApp.initCategories[1] to AmountFormula.Value(BigDecimal("-2.5")),
            TryInitApp.initCategories[2] to AmountFormula.Value(BigDecimal("-30")),
            TryInitApp.initCategories[3] to AmountFormula.Value(BigDecimal("-200")),
        )
        // # When
        val actual = categoryAmounts.fillIntoCategory(TryInitApp.initCategories[0], BigDecimal("-300"))
        // # Then
        assertEquals(
            CategoryAmountFormulas(
                TryInitApp.initCategories[0] to AmountFormula.Value(BigDecimal("-67.5")),
                TryInitApp.initCategories[1] to AmountFormula.Value(BigDecimal("-2.5")),
                TryInitApp.initCategories[2] to AmountFormula.Value(BigDecimal("-30")),
                TryInitApp.initCategories[3] to AmountFormula.Value(BigDecimal("-200")),
            ),
            actual
        )
    }
}