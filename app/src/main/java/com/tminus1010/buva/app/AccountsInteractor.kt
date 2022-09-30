package com.tminus1010.buva.app

import com.tminus1010.buva.data.AccountsRepo
import com.tminus1010.buva.domain.AccountsAggregate
import com.tminus1010.buva.domain.TransactionBlock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountsInteractor @Inject constructor(
    private val accountsRepo: AccountsRepo,
    private val transactionsInteractor: TransactionsInteractor,
) {
    suspend fun guessAccountsTotalInPast(transactionBlock: TransactionBlock): BigDecimal {
        return guessAccountsTotalInPast(transactionBlock.datePeriod!!.startDate)
    }

    suspend fun guessAccountsTotalInPast(date: LocalDate): BigDecimal {
        val incomeUpToCurrentAccountsTotal = transactionsInteractor.transactionBlocks.first()
            .filter { it.datePeriod!!.startDate >= date }
            .fold(BigDecimal.ZERO) { acc, v -> acc + v.total }
        return accountsRepo.accountsAggregate.first().total - incomeUpToCurrentAccountsTotal
    }
}