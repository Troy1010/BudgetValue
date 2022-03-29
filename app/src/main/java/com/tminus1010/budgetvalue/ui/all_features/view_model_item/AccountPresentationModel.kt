package com.tminus1010.budgetvalue.ui.all_features.view_model_item

import com.tminus1010.budgetvalue.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue.data.AccountsRepo
import com.tminus1010.budgetvalue.domain.Account
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