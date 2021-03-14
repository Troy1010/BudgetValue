package com.tminus1010.budgetvalue.extensions

import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import com.tminus1010.tmcommonkotlin.rx.extensions.total
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

// TODO("move test to tmcommonkotlin")
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