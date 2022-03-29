package com.tminus1010.budgetvalue.ui.all_features.view_model_item

import com.tminus1010.budgetvalue.data.AccountsRepo
import com.tminus1010.budgetvalue.domain.AccountsAggregate

class AccountsPresentationModel private constructor(private val accountsAggregate: AccountsAggregate, private val accountPresentationModelList: List<AccountPresentationModel>) : List<AccountPresentationModel> by accountPresentationModelList {
    constructor(accountsAggregate: AccountsAggregate, accountsRepo: AccountsRepo) : this(accountsAggregate, accountsAggregate.accounts.map { AccountPresentationModel(it, accountsRepo) })

    // # State
    val total get() = accountsAggregate.total.toString()
}