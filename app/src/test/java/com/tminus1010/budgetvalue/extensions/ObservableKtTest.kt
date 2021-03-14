package com.tminus1010.budgetvalue.extensions

import com.tminus1010.tmcommonkotlin.rx.extensions.isCold
import com.tminus1010.tmcommonkotlin.rx.extensions.noEnd
import com.tminus1010.tmcommonkotlin.rx.extensions.pairwise
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.Assert.*
import org.junit.Test

// TODO("move test to tmcommonkotlin")
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

    @Test
    fun isCold_GivenCold() {
        // # Given
        val observable = Observable.just(1, 6, 7)
        // # Stimulate & Verify
        assertTrue(observable.isCold())
    }

    @Test
    fun isCold_GivenHot() {
        // # Given
        val subject = PublishSubject.create<Int>()
        subject.onNext(2)
        subject.onNext(9)
        // # Stimulate & Verify
        assertFalse(subject.isCold())
    }

    @Test
    fun isCold_GivenBehaviorSubject() {
        // # Given
        val subject = BehaviorSubject.create<Int>()
        subject.onNext(2)
        subject.onNext(9)
        // # Stimulate & Verify
        assertTrue(subject.isCold())
    }
}