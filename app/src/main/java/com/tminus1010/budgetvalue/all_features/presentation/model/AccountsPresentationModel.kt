package com.tminus1010.budgetvalue.all_features.presentation.model

import com.tminus1010.budgetvalue.all_features.data.repo.AccountsRepo
import com.tminus1010.budgetvalue.all_features.domain.accounts.AccountsAggregate

class AccountsPresentationModel private constructor(private val accountsAggregate: AccountsAggregate, private val accountPresentationModelList: List<AccountPresentationModel>) : List<AccountPresentationModel> by accountPresentationModelList {
    constructor(accountsAggregate: AccountsAggregate, accountsRepo: AccountsRepo) : this(accountsAggregate, accountsAggregate.accounts.map { AccountPresentationModel(it, accountsRepo) })

    // # State
    val total get() = accountsAggregate.total.toString()
}