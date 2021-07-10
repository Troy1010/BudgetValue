package com.tminus1010.budgetvalue._core.extensions

import io.reactivex.rxjava3.core.Completable
import junit.framework.TestCase
import java.util.concurrent.TimeUnit

class CompletableKtTest : TestCase() {

    fun testToDuration() {
        // # Given
        val observable = Completable.timer(2, TimeUnit.SECONDS).toDuration()
        // # When & Then
        observable.test().await().assertValue { it - 2000 < 50 }
    }

    fun testToDuration_Given2Subscriptions() {
        // # Given
        val observable = Completable.timer(4, TimeUnit.SECONDS).toDuration()
        // # When & Then
        val tester1 = observable.test()
        Thread.sleep(400)
        val tester2 = observable.test()
        tester1.await().assertValue { -50 < it - 4000 && it - 4000 < 50 }
        tester2.await().assertValue { -50 < it - 4000 && it - 4000 < 50 }
    }
}