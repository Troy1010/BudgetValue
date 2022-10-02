package com.tminus1010.buva.domain

import com.tminus1010.buva.core_testing.shared.Given
import junit.framework.Assert.fail
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class CategoryAmountsTest {
    @Test
    fun test() {
        // # Given
        val map1 =
            mapOf(
                com.tminus1010.buva.core_testing.shared.Given.categories[0].logx("category 0") to BigDecimal("100"),
                com.tminus1010.buva.core_testing.shared.Given.categories[1].logx("category 1") to BigDecimal("200"),
                com.tminus1010.buva.core_testing.shared.Given.categories[2].logx("category 2") to BigDecimal("100"),
            )
        val map2 = map1.plus(com.tminus1010.buva.core_testing.shared.Given.categories[3] to BigDecimal("40"))
        val sortedMap =
            map2.toSortedMap(compareBy { map1[it] })
                .logx("sortedMap")
        // # When & Then
        try {
            CategoryAmounts(sortedMap)
            fail()
        } catch (e: Throwable) {
            // Success
        }
    }

    @Test
    fun replaceKey() {
        // # Given
        val categoryAmounts =
            CategoryAmounts(
                com.tminus1010.buva.core_testing.shared.Given.categories[0] to BigDecimal("100"),
                com.tminus1010.buva.core_testing.shared.Given.categories[1] to BigDecimal("200"),
                com.tminus1010.buva.core_testing.shared.Given.categories[2] to BigDecimal("300"),
            )
        // # When & Then
        assertEquals(
            CategoryAmounts(
                com.tminus1010.buva.core_testing.shared.Given.categories[6] to BigDecimal("100"),
                com.tminus1010.buva.core_testing.shared.Given.categories[1] to BigDecimal("200"),
                com.tminus1010.buva.core_testing.shared.Given.categories[2] to BigDecimal("300"),
            ),
            categoryAmounts.replaceKey(com.tminus1010.buva.core_testing.shared.Given.categories[0], com.tminus1010.buva.core_testing.shared.Given.categories[6])
        )
    }
}