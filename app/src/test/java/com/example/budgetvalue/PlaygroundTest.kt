package com.example.budgetvalue

import org.junit.Test
import io.reactivex.rxjava3.core.Observable
import org.junit.Assert.assertEquals

class PlaygroundTest() {
    @Test
    fun test1() {
        // # Stimulate
        val result = Observable.just(4, 6, 8, 4, 2)
            .scan(0) { acc, y -> acc + y }
            .toList()
            .blockingGet()
        // # Verify
        assertEquals(listOf(0, 4, 10, 18, 22, 24), result)
    }
}