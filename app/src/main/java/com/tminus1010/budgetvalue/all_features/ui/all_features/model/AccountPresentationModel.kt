package com.tminus1010.budgetvalue.all_features.ui.all_features.model

import com.tminus1010.budgetvalue.all_features.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue.all_features.data.repo.AccountsRepo
import com.tminus1010.budgetvalue.all_features.domain.accounts.Account
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AccountPresentationModel(private val account: Account, private val accountsRepo: AccountsRepo) {
    // # User Intents
    fun userSetTitle(s: String) {
        GlobalScope.launch { accountsRepo.update(account.copy(name = s)) }
    }

    fun userSetAmount(s: String) {
        GlobalScope.launch { accountsRepo.update(account.copy(amount = s.toMoneyBigDecimal())) }
    }

    fun userDeleteAccount() {
        GlobalScope.launch { accountsRepo.delete(account) }
    }

    // # State
    val title get() = account.name
    val amount get() = account.amount.toString()
}