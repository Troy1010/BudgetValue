package com.tminus1010.budgetvalue.accounts.presentation

import com.tminus1010.budgetvalue._core.all.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue.accounts.data.AccountsRepo
import com.tminus1010.budgetvalue.accounts.app.Account

class AccountVMItem(private val account: Account, private val accountsRepo: AccountsRepo) {
    // # User Intents
    fun userSetTitle(s: String) {
        accountsRepo.update(account.copy(name = s)).subscribe()
    }

    fun userSetAmount(s: String) {
        accountsRepo.update(account.copy(amount = s.toMoneyBigDecimal())).subscribe()
    }

    fun userDeleteAccount() {
        accountsRepo.delete(account).subscribe()
    }

    // # State
    val title get() = account.name
    val amount get() = account.amount.toString()
}