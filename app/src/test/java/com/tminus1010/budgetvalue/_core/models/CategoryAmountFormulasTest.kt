package com.tminus1010.budgetvalue._core.models

import com.tminus1010.budgetvalue._core.domain.CategoryAmountFormulas
import com.tminus1010.budgetvalue.app_init.AppInitInteractor
import com.tminus1010.budgetvalue.transactions.app.AmountFormula
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class CategoryAmountFormulasTest {
    @Test
    fun testDefaultAmount() {
        // # Given
        val categoryAmounts = CategoryAmountFormulas(
            AppInitInteractor.initCategories[0] to AmountFormula.Value(BigDecimal("-16.5")),
            AppInitInteractor.initCategories[1] to AmountFormula.Value(BigDecimal("-2.5")),
            AppInitInteractor.initCategories[2] to AmountFormula.Value(BigDecimal("-30")),
            AppInitInteractor.initCategories[3] to AmountFormula.Value(BigDecimal("-200")),
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
            AppInitInteractor.initCategories[0] to AmountFormula.Value(BigDecimal("-16.5")),
            AppInitInteractor.initCategories[1] to AmountFormula.Value(BigDecimal("-2.5")),
            AppInitInteractor.initCategories[2] to AmountFormula.Value(BigDecimal("-30")),
            AppInitInteractor.initCategories[3] to AmountFormula.Value(BigDecimal("-200")),
        )
        // # When
        val actual = categoryAmounts.fillIntoCategory(AppInitInteractor.initCategories[0], BigDecimal("-300"))
        // # Then
        assertEquals(
            CategoryAmountFormulas(
                AppInitInteractor.initCategories[0] to AmountFormula.Value(BigDecimal("-67.5")),
                AppInitInteractor.initCategories[1] to AmountFormula.Value(BigDecimal("-2.5")),
                AppInitInteractor.initCategories[2] to AmountFormula.Value(BigDecimal("-30")),
                AppInitInteractor.initCategories[3] to AmountFormula.Value(BigDecimal("-200")),
            ),
            actual
        )
    }
}