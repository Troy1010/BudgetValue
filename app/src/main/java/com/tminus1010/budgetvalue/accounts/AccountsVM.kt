package com.tminus1010.budgetvalue.accounts

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.extensions.launch
import com.tminus1010.budgetvalue._layer_facades.DomainFacade
import com.tminus1010.budgetvalue.accounts.domain.AccountsDomain
import com.tminus1010.budgetvalue.accounts.domain.IAccountsDomain
import com.tminus1010.budgetvalue.accounts.models.Account
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class AccountsVM @Inject constructor(
    domainFacade: DomainFacade,
    accountsDomain: AccountsDomain,
) : ViewModel(), IAccountsDomain by accountsDomain {
    val intentAddAccount = PublishSubject.create<Unit>()
        .also { it.launch { domainFacade.push(Account("", BigDecimal.ZERO)) } }
    val intentDeleteAccount = PublishSubject.create<Account>()
        .also { it.launch { domainFacade.delete(it) } }
    val intentUpdateAccount = PublishSubject.create<Account>()
        .also { it.launch { domainFacade.update(it) } }
}