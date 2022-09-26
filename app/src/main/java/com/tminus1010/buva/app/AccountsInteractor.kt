package com.tminus1010.buva.app

import com.tminus1010.buva.data.AccountsRepo
import com.tminus1010.buva.domain.TransactionBlock
import kotlinx.coroutines.flow.first
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountsInteractor @Inject constructor(
    private val accountsRepo: AccountsRepo,
    private val transactionsInteractor: TransactionsInteractor,
) {
    suspend fun guessAccountsTotalInPast(transactionBlock: TransactionBlock): BigDecimal? {
        val currentAccountsTotal = accountsRepo.accountsAggregate.first().total ?: return null
        val incomeUpToCurrentAccountsTotal = transactionsInteractor.transactionBlocks.first()
            .filter { it.datePeriod!!.startDate >= transactionBlock.datePeriod!!.startDate }
            .fold(BigDecimal.ZERO) { acc, v -> acc + v.total }
        return currentAccountsTotal - incomeUpToCurrentAccountsTotal
    }
}