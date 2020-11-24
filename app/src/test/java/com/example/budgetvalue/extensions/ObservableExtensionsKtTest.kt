package com.example.budgetvalue.extensions

import io.reactivex.rxjava3.core.Observable
import org.junit.Test

import org.junit.Assert.*

class ObservableExtensionsKtTest {
    @Test
    fun pairwise() {
        // # Given
        val observable = Observable.just(0, 9, 5, 8, 6, 2)
        // # Stimulate
        val result = observable.pairwise().map { it.second - it.first }
        // # Verify
        assertEquals(listOf(9, -4, 3, -2, -4), result.toList().blockingGet())
    }

    @Test
    fun pairwiseDefault() {
        // # Given
        val observable = Observable.just(9, 5, 8, 6, 2)
        // # Stimulate
        val result = observable.pairwise(0).map { it.second - it.first }
        // # Verify
        assertEquals(observable.toList().blockingGet().size, result.toList().blockingGet().size)
        assertEquals(listOf(9, -4, 3, -2, -4), result.toList().blockingGet())
    }

    @Test
    fun pairwise_GivenObservedTwice() {
        // # Given
        val observable = Observable.just(0, 9, 5, 8, 6, 2)
        // # Stimulate
        val pairwiseObservable = observable.pairwise().map { it.second - it.first }
        pairwiseObservable.toList().blockingGet()
        val result = pairwiseObservable.toList().blockingGet()
        // # Verify
        assertEquals(listOf(9, -4, 3, -2, -4), result)
    }
}