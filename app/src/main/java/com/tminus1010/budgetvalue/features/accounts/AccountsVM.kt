package com.tminus1010.budgetvalue.features.accounts

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.extensions.launch
import com.tminus1010.budgetvalue.features_shared.Domain
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal

class AccountsVM(domain: Domain) : ViewModel() {
    val intentAddAccount = PublishSubject.create<Unit>()
        .also { it.launch { domain.push(Account("", BigDecimal.ZERO)) } }
    val intentDeleteAccount = PublishSubject.create<Account>()
        .also { it.launch { domain.delete(it) } }
    val intentUpdateAmmount = PublishSubject.create<Account>()
        .also { it.launch { domain.update(it) } }
    val accounts = domain.fetchAccounts().toBehaviorSubject(emptyList())
    val accountsTotal = accounts
        .map { it.fold(BigDecimal.ZERO) { acc, account -> acc + account.amount } }
        .toBehaviorSubject()
}