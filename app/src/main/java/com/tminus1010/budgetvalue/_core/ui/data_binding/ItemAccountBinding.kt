package com.tminus1010.budgetvalue._core.ui.data_binding

import com.tminus1010.budgetvalue._core.middleware.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.middleware.ui.onDone
import com.tminus1010.budgetvalue.accounts.AccountsVM
import com.tminus1010.budgetvalue.accounts.models.Account
import com.tminus1010.budgetvalue.databinding.ItemAccountBinding

fun ItemAccountBinding.bind(account: Account, accountsVM: AccountsVM) {
    btnDeleteAccount.setOnClickListener { accountsVM.deleteAccount(account) }
    editTextName.setText(account.name)
    editTextName.onDone { accountsVM.updateAccount(account.copy(name = it)) }
    editTextAmount.setText(account.amount.toString())
    editTextAmount.onDone { accountsVM.updateAccount(account.copy(amount = it.toMoneyBigDecimal())) }
}