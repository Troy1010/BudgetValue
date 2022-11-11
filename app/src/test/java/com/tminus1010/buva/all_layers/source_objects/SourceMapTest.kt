package com.tminus1010.buva.all_layers.source_objects

import com.tminus1010.buva.all_layers.extensions.value
import com.tminus1010.buva.core_testing.shared.test_observer.test
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.ResetStrategy
import kotlinx.coroutines.GlobalScope
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class SourceMapTest {
    @Test
    fun testEmptyConstruction() {
        // # Given
        val sourceMap = SourceMap<String, Int>(GlobalScope)
        // # When
        /* do nothing */
        // # Then
        assertEquals(mapOf<String, Int>(), sourceMap.map.value)
    }

    @Test
    fun testBasicAdjustTo() {
        // # Given
        val sourceMap = SourceMap<String, Int>(GlobalScope)
        // # When
        val y = mapOf("0" to 0)
        sourceMap.adjustTo(y)
        // # Then
        assertEquals(y, sourceMap.map.value)
    }

    @Test
    fun testBasicItemFlowMap() {
        // # Given
        val sourceMap = SourceMap<String, Int>(GlobalScope)
        // # When
        val testObserver = sourceMap.itemFlowMap.test()
        val y = mapOf("0" to 0)
        sourceMap.adjustTo(y)
        Thread.sleep(1000)
        // # Then
        assertEquals(y, testObserver.values().last().mapValues { it.value.value })
    }

    @Test
    fun testItemFlowMap() {
        // # Given
        val sourceMap = SourceMap<Category, Int>(GlobalScope)
        // # When
        val testObserver = sourceMap.itemFlowMap.test()
        sourceMap.adjustTo(mapOf(Category("name1", resetStrategy = ResetStrategy.Basic(BigDecimal("4"))) to 4))
        sourceMap.adjustTo(mapOf(Category("name1", resetStrategy = ResetStrategy.Basic(BigDecimal("5"))) to 6))
        Thread.sleep(1000)
        // # Then
        assertEquals(
            mapOf(Category("name1", resetStrategy = ResetStrategy.Basic(BigDecimal("5"))) to 6),
            testObserver.values().last().mapValues { it.value.value },
        )
    }
}