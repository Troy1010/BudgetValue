package com.example.budgetvalue.extensions

import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.core.Observable
import org.junit.Test

import org.junit.Assert.*

class ObservableKtTest {
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


    // No replay functionality, even with .replay()
    @Test
    fun noReplayFunctionality_GivenUsingReplay() {
        // # Given
        var count = 0
        val observable = Observable.just(55)
            .doOnNext { count++ }
            .replay(1).refCount()
        // # Stimulate
        observable.subscribe()
        observable.subscribe()
        // # Verify
        assertEquals(2, count)
    }

    // Replay functionality via noEnd
    @Test
    fun noEnd() {
        // # Given
        var count = 0
        val observable = Observable.just(55).noEnd()
            .doOnNext { count++ }
            .replay(1).refCount()
        // # Stimulate
        observable.subscribe()
        observable.subscribe()
        // # Verify
        assertEquals(1, count)
    }

    // Replay functionality via BehaviorSubject
    @Test
    fun replayFunctionality_GivenUsingToBehaviorSubject() {
        // # Given
        var count = 0
        val observable = Observable.just(55)
            .doOnNext { count++ }
            .toBehaviorSubject()
        // # Stimulate
        observable.subscribe()
        observable.subscribe()
        // # Verify
        assertEquals(1, count)
    }
}