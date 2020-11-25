package com.example.budgetvalue.layer_data

import androidx.test.platform.app.InstrumentationRegistry
import com.example.budgetvalue.AppMock
import com.example.budgetvalue.model_data.Account
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class MyDaoWrapperTest {
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
    fun update_ObservationCount() {
        // # Given
        repo.add(Account("bank", BigDecimal.ONE)).blockingAwait()
        val account = repo.getAccounts().blockingFirst()[0]
        var observationCount = 0
        repo.getAccounts().subscribe { observationCount += 1 }
        // # Stimulate
        repo.update(account.copy(amount = BigDecimal.TEN)).blockingAwait()
        repo.update(account.copy(amount = BigDecimal.TEN)).blockingAwait()
        repo.update(account.copy(amount = BigDecimal.TEN)).blockingAwait()
        // # Verify
        assertEquals(2, observationCount)
    }
}