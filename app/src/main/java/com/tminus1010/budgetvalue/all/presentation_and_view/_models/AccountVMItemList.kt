package com.tminus1010.budgetvalue.all.presentation_and_view._models

import com.tminus1010.budgetvalue.all.data.repos.AccountsRepo
import com.tminus1010.budgetvalue.all.domain.models.AccountList

class AccountVMItemList private constructor(private val accountList: AccountList, private val accountVMItemList: List<AccountVMItem>) : List<AccountVMItem> by accountVMItemList {
    constructor(accountList: AccountList, accountsRepo: AccountsRepo) : this(accountList, accountList.accounts.map { AccountVMItem(it, accountsRepo) })

    // # Presentation State
    val total get() = accountList.total.toString()
}