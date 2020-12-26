package com.tminus1010.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.model_data.Account
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal

class AccountsVM(private val repo: Repo): ViewModel() {
    val accounts = repo.getAccounts().toBehaviorSubject()
    val intentAddAccount = PublishSubject.create<Unit>()
        .also { it.observeOn(Schedulers.io()).flatMapCompletable { repo.add(Account("", BigDecimal.ZERO)) }.subscribe() }
    val intentDeleteAccount = PublishSubject.create<Account>()
        .also { it.observeOn(Schedulers.io()).flatMapCompletable { repo.delete(it) }.subscribe() }
    val accountsTotal = accounts
        .map { it.fold(BigDecimal.ZERO) { acc, account -> acc + account.amount } }
        .toBehaviorSubject()

    fun updateAccount(account: Account) {
        repo.update(account).subscribe()
    }
}