package com.tminus1010.budgetvalue

import io.reactivex.rxjava3.core.Observable
import org.junit.Test

import org.junit.Assert.*
import java.util.concurrent.TimeUnit

class MiscKtTest {

    @Test
    fun mergeCombineWithIndex() {
        // # Given
        val a = Observable.just("a")
        val b = Observable.just("b").delay(1, TimeUnit.SECONDS)
        val c = Observable.just("c")
            .delay(2, TimeUnit.SECONDS)
            .startWith(Observable.just("d").delay(2, TimeUnit.SECONDS))
        // # Stimulate
        val result = mergeCombineWithIndex(a, b, c).toList().blockingGet()
        // # Verify
        assertEquals(listOf(0, 1, 2, 2), result.map { it.first })
    }
}