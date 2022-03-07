package com.tminus1010.budgetvalue._core.all.extensions

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class ObservableKtTest {

    @ParameterizedTest
    @MethodSource("getData")
    fun flatMapSourceList(givenList1: List<Int>, givenList2: List<Int>, givenList3: List<Int>, expectedList: List<Int>) = runBlocking {
        // # Given
        val givenFlow =
            flow {
                emit(listOf(1, 2, 3))
                delay(100)
                emit(listOf(1, 2, 4))
                delay(200)
                emit(listOf(1, 2, 5))
            }
        // # When
        val result =
            givenFlow.flatMapSourceList { it.flow }
                .stateIn(GlobalScope, SharingStarted.Eagerly, listOf())
        Thread.sleep(5000)
        // # Then
        assertEquals(listOf(1, 2, 5), result.value)
    }

    companion object {
        @JvmStatic
        fun getData(): List<Arguments> {
            return listOf(
                Arguments.of(listOf(1, 2, 3), listOf(1, 2, 4), listOf(1, 2, 5), listOf(1, 2, 5)),
                Arguments.of(listOf(1, 2), listOf(1, 2, 4, 7), listOf(1, 2, 5), listOf(1, 2, 5)),
                Arguments.of(listOf(1, 2, 3, 4), listOf(1, 2), listOf(1, 2, 5), listOf(1, 2, 5)),
            )
        }
    }
}