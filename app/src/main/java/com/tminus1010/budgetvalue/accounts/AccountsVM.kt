package com.tminus1010.budgetvalue.accounts

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.accounts.domain.AccountsDomain
import com.tminus1010.budgetvalue.accounts.domain.IAccountsDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountsVM @Inject constructor(
    private val accountsDomain: AccountsDomain,
) : ViewModel(), IAccountsDomain by accountsDomain