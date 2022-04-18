package com.tminus1010.buva.ui.all_features.view_model_item

import com.tminus1010.buva.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.buva.data.AccountsRepo
import com.tminus1010.buva.domain.Account
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