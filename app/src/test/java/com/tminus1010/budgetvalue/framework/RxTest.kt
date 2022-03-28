package com.tminus1010.budgetvalue.framework

import com.tminus1010.budgetvalue.framework.observable.Rx
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Test

class RxTest {

    suspend fun waitThenEmit(): Int {
        Thread.sleep(1000)
        return 1337
    }

    @Test
    fun completableFromSuspend_disposeShouldNotThrowException() {
        val completable =
            Rx.completableFromSuspend { waitThenEmit() }
                .subscribeOn(Schedulers.io())
        // # When
        val d = completable.test()
        Thread.sleep(300)
        d.dispose()
        // # Then
        // no exception
        Thread.sleep(2000)
    }
}