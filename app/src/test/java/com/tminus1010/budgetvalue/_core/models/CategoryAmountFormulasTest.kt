package com.tminus1010.budgetvalue._core.models

import com.tminus1010.budgetvalue._core.domain.CategoryAmountFormulas
import com.tminus1010.budgetvalue.app_init.AppInteractor
import com.tminus1010.budgetvalue.transactions.app.AmountFormula
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class CategoryAmountFormulasTest {

    @Test
    fun testDefaultAmount() {
        // # Given
        val categoryAmounts = CategoryAmountFormulas(
            AppInteractor.initCategories[0] to AmountFormula.Value(BigDecimal("-16.5")),
            AppInteractor.initCategories[1] to AmountFormula.Value(BigDecimal("-2.5")),
            AppInteractor.initCategories[2] to AmountFormula.Value(BigDecimal("-30")),
            AppInteractor.initCategories[3] to AmountFormula.Value(BigDecimal("-200")),
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
            AppInteractor.initCategories[0] to AmountFormula.Value(BigDecimal("-16.5")),
            AppInteractor.initCategories[1] to AmountFormula.Value(BigDecimal("-2.5")),
            AppInteractor.initCategories[2] to AmountFormula.Value(BigDecimal("-30")),
            AppInteractor.initCategories[3] to AmountFormula.Value(BigDecimal("-200")),
        )
        // # When
        val actual = categoryAmounts.fillIntoCategory(AppInteractor.initCategories[0], BigDecimal("-300"))
        // # Then
        assertEquals(
            CategoryAmountFormulas(
                AppInteractor.initCategories[0] to AmountFormula.Value(BigDecimal("-67.5")),
                AppInteractor.initCategories[1] to AmountFormula.Value(BigDecimal("-2.5")),
                AppInteractor.initCategories[2] to AmountFormula.Value(BigDecimal("-30")),
                AppInteractor.initCategories[3] to AmountFormula.Value(BigDecimal("-200")),
            ),
            actual
        )
    }
}