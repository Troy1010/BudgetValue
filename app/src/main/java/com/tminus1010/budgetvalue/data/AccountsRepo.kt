package com.tminus1010.budgetvalue.data

import com.tminus1010.budgetvalue.data.service.MiscDAO
import com.tminus1010.budgetvalue.domain.accounts.Account
import com.tminus1010.budgetvalue.domain.accounts.AccountsAggregate
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountsRepo @Inject constructor(
    private val miscDAO: MiscDAO,
) {
    val accountsAggregate =
        miscDAO.fetchAccounts()
            .map(::AccountsAggregate)
            .shareIn(GlobalScope, SharingStarted.WhileSubscribed(), 1)

    suspend fun update(account: Account) {
        if (miscDAO.getAccount(account.id) != account)
            miscDAO.update(account)
    }

    suspend fun add(account: Account) {
        miscDAO.insert(account)
    }

    suspend fun delete(account: Account) {
        miscDAO.delete(account)
    }
}