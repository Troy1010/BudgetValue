package com.tminus1010.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.extensions.launch
import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.layer_domain.Domain
import com.tminus1010.budgetvalue.model_domain.Account
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal

class AccountsVM(repo: Repo, domain: Domain) : ViewModel() {
    val intentAddAccount = PublishSubject.create<Unit>()
        .also { it.launch { repo.add(domain.toAccountDTO(Account("", BigDecimal.ZERO))) } }
    val intentDeleteAccount = PublishSubject.create<Account>()
        .also { it.launch { repo.delete(domain.toAccountDTO(it)) } }
    val intentUpdateAmmount = PublishSubject.create<Account>()
        .also { it.launch { repo.update(domain.toAccountDTO(it)) } }
    val accounts = repo.getAccounts().map { it.map { domain.toAccount(it) } }.toBehaviorSubject()
    val accountsTotal = accounts
        .map { it.fold(BigDecimal.ZERO) { acc, account -> acc + account.amount } }
        .toBehaviorSubject()
}