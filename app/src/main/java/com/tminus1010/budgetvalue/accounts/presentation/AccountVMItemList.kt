package com.tminus1010.budgetvalue.accounts.presentation

import com.tminus1010.budgetvalue._core.data.repo.AccountsRepo
import com.tminus1010.budgetvalue._core.domain.accounts.AccountsAggregate

class AccountVMItemList private constructor(private val accountsAggregate: AccountsAggregate, private val accountVMItemList: List<AccountVMItem>) : List<AccountVMItem> by accountVMItemList {
    constructor(accountsAggregate: AccountsAggregate, accountsRepo: AccountsRepo) : this(accountsAggregate, accountsAggregate.accounts.map { AccountVMItem(it, accountsRepo) })

    // # State
    val total get() = accountsAggregate.total.toString()
}