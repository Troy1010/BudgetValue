package com.tminus1010.budgetvalue.all.presentation_and_view._models

import com.tminus1010.budgetvalue.all.data.repos.AccountsRepo
import com.tminus1010.budgetvalue.all.domain.models.AccountsAggregate

class AccountVMItemList private constructor(private val accountsAggregate: AccountsAggregate, private val accountVMItemList: List<AccountVMItem>) : List<AccountVMItem> by accountVMItemList {
    constructor(accountsAggregate: AccountsAggregate, accountsRepo: AccountsRepo) : this(accountsAggregate, accountsAggregate.accounts.map { AccountVMItem(it, accountsRepo) })

    // # Presentation State
    val total get() = accountsAggregate.total.toString()
}