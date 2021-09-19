package com.tminus1010.budgetvalue.all.presentation_and_view._view_model_item

import com.tminus1010.budgetvalue.all.domain.models.Accounts

class AccountsVMItem(private val accounts: Accounts) {
    // # Presentation Output
    val total get() = accounts.total.toString()
}