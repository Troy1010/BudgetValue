package com.tminus1010.buva.data

import com.tminus1010.buva.domain.Account
import com.tminus1010.buva.domain.AccountsAggregate
import com.tminus1010.buva.domain.AccountsUpdateInfo
import com.tminus1010.buva.environment.MiscDAO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import java.time.LocalDate
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
    val accountsUpdateInfos =
        miscDAO.fetchAccountsUpdateInfo()

    suspend fun update(account: Account) {
        if (miscDAO.getAccount(account.id) != account)
            miscDAO.update(account)
        miscDAO.push(
            AccountsUpdateInfo(
                date = LocalDate.now()
            )
        )
    }

    suspend fun add(account: Account) {
        miscDAO.insert(account)
        miscDAO.push(
            AccountsUpdateInfo(
                date = LocalDate.now()
            )
        )
    }

    suspend fun delete(account: Account) {
        miscDAO.delete(account)
        miscDAO.push(
            AccountsUpdateInfo(
                date = LocalDate.now()
            )
        )
    }
}