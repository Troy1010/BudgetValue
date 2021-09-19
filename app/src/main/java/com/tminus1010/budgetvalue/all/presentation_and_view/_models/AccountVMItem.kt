package com.tminus1010.budgetvalue.all.presentation_and_view._models

import com.tminus1010.budgetvalue._core.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue.all.data.repos.AccountsRepo
import com.tminus1010.budgetvalue.all.domain.models.Account

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

    // # Presentation Output
    val title get() = account.name
    val amount get() = account.amount.toString()
}