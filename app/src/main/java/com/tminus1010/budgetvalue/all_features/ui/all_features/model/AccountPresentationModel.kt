package com.tminus1010.budgetvalue.all_features.ui.all_features.model

import com.tminus1010.budgetvalue.all_features.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue.all_features.data.repo.AccountsRepo
import com.tminus1010.budgetvalue.all_features.domain.accounts.Account

class AccountPresentationModel(private val account: Account, private val accountsRepo: AccountsRepo) {
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