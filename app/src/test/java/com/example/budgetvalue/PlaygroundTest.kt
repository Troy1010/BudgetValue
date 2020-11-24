package com.example.budgetvalue

import com.example.budgetvalue.extensions.withLatestFrom
import org.junit.Test
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import org.junit.Assert.assertEquals

class PlaygroundTest() {
    @Test
    fun test1() {
        // # Stimulate
        val observable = Observable.just(4, 6, 8, 4, 2)
            .compose { observable ->
                observable.skip(1).zipWith(observable, BiFunction<Int, Int, Pair<Int, Int>> { a, b -> Pair(a,b)})
            }
        val result = observable.toList().blockingGet()
        // # Verify
        assertEquals(listOf(0, 4, 10, 18, 22, 24), result)
    }
}