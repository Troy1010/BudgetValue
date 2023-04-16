package com.tminus1010.buva.app

import com.tminus1010.buva.data.AccountsRepo
import com.tminus1010.buva.data.TransactionsRepo
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.Period
import javax.inject.Inject

class IsReadyToBudget @Inject constructor(
    private val transactionsInteractor: TransactionsInteractor,
    private val accountsRepo: AccountsRepo,
    private val transactionsRepo: TransactionsRepo,
) {
    class CategorizationIsNotCompleteException : Exception()
    class AccountsNotUpdatedRecentlyException : Exception()
    class NoRecentTransactionImportItemException : Exception()

    suspend fun check() {
        if (!transactionsInteractor.transactionsAggregate.first().areAllSpendsCategorized)
            throw CategorizationIsNotCompleteException()
        else if (
            accountsRepo.accountsAggregate.first().accounts.isEmpty()
            || accountsRepo.accountsUpdateInfos.first().map { it.date }.maxByOrNull { it }
                ?.let { Period.between(it, LocalDate.now()).days > 7 } ?: true
        )
            throw AccountsNotUpdatedRecentlyException()
        else if (
            transactionsRepo.mostRecentImportItemDate.first()
                ?.let { Period.between(it, LocalDate.now()).days > 7 }
                ?: true
        )
            throw NoRecentTransactionImportItemException()
    }
}

suspend fun IsReadyToBudget.isReady() = runCatching { check(); true }.getOrDefault(false)