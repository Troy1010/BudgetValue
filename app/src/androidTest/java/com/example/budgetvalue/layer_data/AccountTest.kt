package com.example.budgetvalue.layer_data

import androidx.test.platform.app.InstrumentationRegistry
import com.example.budgetvalue.AppMock
import com.example.budgetvalue.model_data.Account
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class AccountTest {
    val app by lazy { InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as AppMock }
    val repo by lazy { app.appComponent.getRepo() }

    @Before
    fun before() {
        repo.clearAccounts().blockingAwait()
    }

    @Test
    fun update() {
        // # Given
        repo.add(Account("Bank", BigDecimal.ONE)).blockingAwait()
        val account = repo.getAccounts().blockingFirst()[0]
        assertEquals(1, repo.getAccounts().blockingFirst().size)
        assertEquals(BigDecimal.ONE, repo.getAccounts().blockingFirst()[0].amount)
        // # Stimulate
        repo.update(account.copy(amount = BigDecimal.TEN)).blockingAwait()
        // # Verify
        assertEquals(1, repo.getAccounts().blockingFirst().size)
        assertEquals(BigDecimal.TEN, repo.getAccounts().blockingFirst()[0].amount)
    }

    @Test
    fun getAccounts_MultipleTimes() {
        // # Given
        repo.add(Account("bank", BigDecimal.ONE)).blockingAwait()
        repo.add(Account("paypal", BigDecimal.TEN)).blockingAwait()
        // # Stimulate & Verify
        assertEquals(2, repo.getAccounts().blockingFirst().size)
        assertEquals(2, repo.getAccounts().blockingFirst().size)
        assertEquals(2, repo.getAccounts().blockingFirst().size)
        assertEquals(2, repo.getAccounts().blockingFirst().size)
    }

    @Test
    fun add_ObservationCount() {
        // # Given
        Thread.sleep(10) // TODO("These sleeps should not be necessary")
        var observationCount = 0
        repo.getAccounts().subscribeOn(Schedulers.trampoline()).subscribe { observationCount += 1 } // TODO("handle disposables")
        assertEquals(0, repo.getAccounts().blockingFirst().size)
        // # Stimulate
        repo.add(Account("bank", BigDecimal.ONE)).blockingAwait()
        Thread.sleep(10)
        repo.add(Account("paypal", BigDecimal.TEN)).blockingAwait()
        Thread.sleep(10)
        repo.add(Account("cash", BigDecimal.ZERO)).blockingAwait()
        Thread.sleep(10)
        // # Verify
        assertEquals(4, observationCount)
    }

    @Test
    fun update_ObservationCount() {
        // # Given
        repo.add(Account("bank", BigDecimal.ONE)).blockingAwait()
        Thread.sleep(10)
        var observationCount = 0
        repo.getAccounts().subscribeOn(Schedulers.trampoline()).subscribe { observationCount += 1 }
        val account = repo.getAccounts().blockingFirst()[0]
        // # Stimulate
        repo.update(account.copy(amount = BigDecimal.TEN)).blockingAwait()
        Thread.sleep(10)
        repo.update(account.copy(amount = BigDecimal.TEN)).blockingAwait()
        Thread.sleep(10)
        repo.update(account.copy(amount = BigDecimal.TEN)).blockingAwait()
        Thread.sleep(10)
        // # Verify
        assertEquals(2, observationCount)
    }
}