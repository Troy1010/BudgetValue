package com.tminus1010.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.extensions.io
import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.model_data.Account
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import javax.inject.Inject

class AccountsVM @Inject constructor(repo: Repo) : ViewModel() {
    val intentAddAccount = PublishSubject.create<Unit>()
        .also { it.io().flatMapCompletable { repo.add(Account("", BigDecimal.ZERO)) }.subscribe() }
    val intentDeleteAccount = PublishSubject.create<Account>()
        .also { it.io().flatMapCompletable { repo.delete(it) }.subscribe() }
    val intentUpdateAmmount = PublishSubject.create<Account>()
        .also { it.io().flatMapCompletable { repo.update(it) }.subscribe() }
    val accounts = repo.getAccounts().toBehaviorSubject()
    val accountsTotal = accounts
        .map { it.fold(BigDecimal.ZERO) { acc, account -> acc + account.amount } }
        .toBehaviorSubject()
}