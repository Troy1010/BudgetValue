package com.tminus1010.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.extensions.io
import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.layer_domain.Domain
import com.tminus1010.budgetvalue.model_data.AccountDTO
import com.tminus1010.budgetvalue.model_domain.Account
import com.tminus1010.tmcommonkotlin.rx.extensions.launch
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal

class AccountsVM(repo: Repo, domain: Domain) : ViewModel() {
    val intentAddAccount = PublishSubject.create<Unit>()
        .also { it.io().flatMapCompletable { repo.add(domain.toAccountDTO(Account("", BigDecimal.ZERO))) }.subscribe() }
    val intentDeleteAccount = PublishSubject.create<Account>()
        .also { it.io().flatMapCompletable { repo.delete(domain.toAccountDTO(it)) }.subscribe() }
    val intentUpdateAmmount = PublishSubject.create<Account>()
        .also { it.io().flatMapCompletable { repo.update(domain.toAccountDTO(it)) }.subscribe() }
    val accounts = repo.getAccounts().map { it.map { domain.toAccount(it) } }.toBehaviorSubject()
    val accountsTotal = accounts
        .map { it.fold(BigDecimal.ZERO) { acc, account -> acc + account.amount } }
        .toBehaviorSubject()
}