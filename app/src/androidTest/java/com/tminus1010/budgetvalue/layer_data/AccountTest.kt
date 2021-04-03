package com.tminus1010.budgetvalue.layer_data

import androidx.test.platform.app.InstrumentationRegistry
import com.tminus1010.budgetvalue.AppMock
import com.tminus1010.budgetvalue.accounts.models.AccountDTO
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
        repo.add(AccountDTO("Bank", BigDecimal.ONE)).blockingAwait()
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
        repo.add(AccountDTO("bank", BigDecimal.ONE)).blockingAwait()
        repo.add(AccountDTO("paypal", BigDecimal.TEN)).blockingAwait()
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
        repo.add(AccountDTO("bank", BigDecimal.ONE)).blockingAwait()
        Thread.sleep(10)
        repo.add(AccountDTO("paypal", BigDecimal.TEN)).blockingAwait()
        Thread.sleep(10)
        repo.add(AccountDTO("cash", BigDecimal.ZERO)).blockingAwait()
        Thread.sleep(10)
        // # Verify
        assertEquals(4, observationCount)
    }

    @Test
    fun update_ObservationCount() {
        // # Given
        repo.add(AccountDTO("bank", BigDecimal.ONE)).blockingAwait()
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