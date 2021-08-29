package com.tminus1010.budgetvalue._core.models

import com.tminus1010.budgetvalue.Given
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import io.reactivex.rxjava3.core.Observable
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class CategoryAmountFormulaVMItemTest {

    @Test
    fun getAmountFormula() {
        // # Given
        val givenCategory = Given.categories[0]
        // # When
        val categoryAmountFormulaVMItem = CategoryAmountFormulaVMItem(
            category = givenCategory,
            _amountFormula = Observable.just(AmountFormula.Value(BigDecimal("123"))),
            fillCategoryAmountFormula = Observable.just(Pair(Given.categories[0], AmountFormula.Value(BigDecimal("555")))),
        )
        // # Then
        assertEquals(
            AmountFormula.Value(BigDecimal("555")),
            categoryAmountFormulaVMItem.amountFormula.value!!,
        )
    }
}