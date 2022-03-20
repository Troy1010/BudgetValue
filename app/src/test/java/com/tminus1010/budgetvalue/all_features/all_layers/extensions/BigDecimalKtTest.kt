package com.tminus1010.budgetvalue.all_features.all_layers.extensions

import org.junit.Test

import org.junit.Assert.*
import java.math.BigDecimal

class BigDecimalKtTest {

    @Test
    fun easyEquals() {
        // # Given
        val givenBigDecimal1 = BigDecimal.TEN
        val givenBigDecimal2 = BigDecimal.TEN
        // # When
        val result = givenBigDecimal1.easyEquals(givenBigDecimal2)
        // # Then
        assertTrue(result)
    }
}