package com.tminus1010.budgetvalue._core.framework.source_objects

import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class SourceListTest {
    @ParameterizedTest
    @MethodSource("getData")
    fun adjustTo(givenSourceList: SourceList<Int>, givenList: List<Int>) = runBlocking {
        // # When
        givenSourceList.adjustTo(givenList)
        // # Then
        assertEquals(givenSourceList, givenList)
        logz("sourceList:$givenSourceList newList:$givenList")
    }

    companion object {
        @JvmStatic
        fun getData(): List<Arguments> {
            return listOf(
                Arguments.of(SourceList(1, 4, 8, 2), listOf(1, 4, 8, 2, 9, 7)),
                Arguments.of(SourceList(1, 4, 8, 2), listOf(2, 4, 8, 2, 9, 7)),
                Arguments.of(SourceList(1, 4, 8, 2), listOf(1, 4, 9, 7)),
            )
        }
    }
}