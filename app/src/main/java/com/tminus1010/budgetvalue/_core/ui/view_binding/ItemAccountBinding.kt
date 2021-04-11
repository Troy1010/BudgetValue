package com.tminus1010.budgetvalue._core.ui.view_binding

import com.tminus1010.budgetvalue._core.middleware.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.middleware.ui.setOnDoneListener
import com.tminus1010.budgetvalue.accounts.AccountsVM
import com.tminus1010.budgetvalue.accounts.models.Account
import com.tminus1010.budgetvalue.databinding.ItemAccountBinding

fun ItemAccountBinding.bind(account: Account, accountsVM: AccountsVM) {
    btnDeleteAccount.setOnClickListener { accountsVM.deleteAccount(account) }
    editTextName.setText(account.name)
    editTextName.setOnDoneListener { accountsVM.updateAccount(account.copy(name = it)) }
    editTextAmount.setText(account.amount.toString())
    editTextAmount.setOnDoneListener { accountsVM.updateAccount(account.copy(amount = it.toMoneyBigDecimal())) }
}