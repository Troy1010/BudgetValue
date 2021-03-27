package com.tminus1010.budgetvalue.accounts

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.extensions.launch
import com.tminus1010.budgetvalue._layer_facades.DomainFacade
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal

class AccountsVM(domainFacade: DomainFacade) : ViewModel() {
    val intentAddAccount = PublishSubject.create<Unit>()
        .also { it.launch { domainFacade.push(Account("", BigDecimal.ZERO)) } }
    val intentDeleteAccount = PublishSubject.create<Account>()
        .also { it.launch { domainFacade.delete(it) } }
    val intentUpdateAccount = PublishSubject.create<Account>()
        .also { it.launch { domainFacade.update(it) } }
    val accounts = domainFacade.fetchAccounts().toBehaviorSubject(emptyList())
    val accountsTotal = accounts
        .map { it.fold(BigDecimal.ZERO) { acc, account -> acc + account.amount } }
        .toBehaviorSubject()
}