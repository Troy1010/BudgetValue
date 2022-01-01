package com.tminus1010.budgetvalue._core.domain

import com.tminus1010.budgetvalue.Given
import com.tminus1010.tmcommonkotlin.core.logx
import junit.framework.Assert.fail
import org.junit.Test
import java.math.BigDecimal

class CategoryAmountsTest {
    @Test
    fun test() {
        // # Given
        val map1 =
            mapOf(
                Given.categories[0].logx("category 0") to BigDecimal("100"),
                Given.categories[1].logx("category 1") to BigDecimal("200"),
                Given.categories[2].logx("category 2") to BigDecimal("100"),
            )
        val map2 = map1.plus(Given.categories[3] to BigDecimal("40"))
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
}