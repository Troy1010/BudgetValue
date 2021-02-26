 package com.tminus1010.budgetvalue.extensions

import com.tminus1010.
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.junit.Test

import org.junit.Assert.*
import java.math.BigDecimal

class IterableKtTest {

    @Test
    fun total() {
        // # Given
        val list = listOf(
            BehaviorSubject.createDefault(BigDecimal(13)),
            BehaviorSubject.createDefault(BigDecimal(9)),
            BehaviorSubject.createDefault(BigDecimal(98))
        )
        // # Stimulate
        val result = list.total().toBehaviorSubject()
        // # Verify
        assertEquals(120.toBigDecimal(), result.value)
    }
}