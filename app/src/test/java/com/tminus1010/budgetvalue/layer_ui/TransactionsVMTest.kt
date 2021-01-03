package com.tminus1010.budgetvalue.layer_ui

import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.model_app.Block
import com.tminus1010.budgetvalue.model_app.LocalDatePeriod
import com.tminus1010.budgetvalue.model_app.Transaction
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Observable
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.Month

class TransactionsVMTest {
    val repo = mockk<Repo>()
        .also { every { it.transactions } returns Observable.just(listOf()) }
        .also { every { it.fetchAnchorDateOffset() } returns Observable.just(0) }
        .also { every { it.fetchBlockSize() } returns Observable.just(14) }
    val datePeriodGetter = DatePeriodGetter(repo)
    val transactionsVM = TransactionsVM(repo, datePeriodGetter)

    @Test
    fun getBlocksFromTransactions() {
        // # Given
        val transactions = listOf(
            Transaction(
                LocalDate.of(2020, Month.JANUARY, 22),
                "",
                14.52.toBigDecimal(),
                hashMapOf(),
                1
            ),
            Transaction(
                LocalDate.of(2020, Month.JANUARY, 1),
                "",
                5.toBigDecimal(),
                hashMapOf(),
                2
            ),
            Transaction(
                LocalDate.of(2020, Month.JANUARY, 1),
                "",
                10.toBigDecimal(),
                hashMapOf(),
                3
            ),
            Transaction(
                LocalDate.of(2020, Month.FEBRUARY, 22),
                "",
                111.11.toBigDecimal(),
                hashMapOf(),
                4
            )
        )
        // # Stimulate
        val result = transactionsVM.getBlocksFromTransactions(transactions)
        // # Verify
        assertEquals(
            listOf(
                Block(LocalDatePeriod(LocalDate.parse("2020-01-01"), LocalDate.parse("2020-01-14")),
                    15.toBigDecimal(),
                    hashMapOf()),
                Block(LocalDatePeriod(LocalDate.parse("2020-01-15"), LocalDate.parse("2020-01-29")),
                    14.52.toBigDecimal(),
                    hashMapOf()),
                Block(LocalDatePeriod(LocalDate.parse("2020-02-14"), LocalDate.parse("2020-02-28")),
                    111.11.toBigDecimal(),
                    hashMapOf())
            ),
            result
        )
    }
}