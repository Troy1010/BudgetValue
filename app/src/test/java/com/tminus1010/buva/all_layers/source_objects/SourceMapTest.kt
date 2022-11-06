package com.tminus1010.buva.all_layers.source_objects

import com.tminus1010.buva.core_testing.shared.test_observer.test
import kotlinx.coroutines.GlobalScope
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

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
}