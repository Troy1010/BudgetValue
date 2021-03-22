package com.tminus1010.budgetvalue.layer_data

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tminus1010.budgetvalue.AppMock
import com.tminus1010.budgetvalue.modules.transactions.TransactionDTO
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class TransactionDTOTest {
    val app by lazy { InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as AppMock }
    val repo by lazy { app.appComponent.getRepo() }

    @Before
    fun before() {
        repo.clearTransactions().blockingAwait()
    }

    @Test
    fun addAndGetTransactionReceived() {
        // # Given
        val transactionReceived = TransactionDTO(LocalDate.now(), "zoop", 1.5.toBigDecimal(), hashMapOf(), 1)
        // # Stimulate
        repo.add(transactionReceived).blockingAwait()
        // # Verify
        assertEquals(1, repo.getTransactionsReceived().blockingFirst().size)
        assertEquals(transactionReceived, repo.getTransactionsReceived().blockingFirst()[0])
    }

    @Test
    fun addAndGetTransactionReceived_GivenList() {
        // # Given
        val transactionsReceived = listOf(
            TransactionDTO(LocalDate.now(), "zoop", 1.5.toBigDecimal()),
            TransactionDTO(LocalDate.now(), "zoop", 1.5.toBigDecimal()),
            TransactionDTO(LocalDate.now(), "zoop", 1.5.toBigDecimal())
        )
        // # Stimulate
        repo.add(transactionsReceived).blockingAwait()
        // # Verify
        assertEquals(3, repo.getTransactionsReceived().blockingFirst().size)
    }

    @Test
    fun clearTransactions() {
        // # Given
        repo.add(TransactionDTO(LocalDate.now(), "zoopA", 1.5.toBigDecimal())).blockingAwait()
        repo.add(TransactionDTO(LocalDate.now(), "zoopB", 1.5.toBigDecimal())).blockingAwait()
        repo.add(TransactionDTO(LocalDate.now(), "zoopC", 1.5.toBigDecimal())).blockingAwait()
        assertEquals(3, repo.getTransactionsReceived().blockingFirst().size)
        // # Stimulate
        repo.clearTransactions().blockingAwait()
        // # Verify
        assertEquals(0, repo.getTransactionsReceived().blockingFirst().size)
    }
}