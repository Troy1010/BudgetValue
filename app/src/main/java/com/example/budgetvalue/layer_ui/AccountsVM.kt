package com.example.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetvalue.layer_data.Repo
import com.example.budgetvalue.model_data.Account
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.launch
import java.math.BigDecimal

class AccountsVM(private val repo: Repo): ViewModel() {
    val disposables = CompositeDisposable()
    val accounts = repo.getAccounts().toBehaviorSubject()
    val intentAddAccount = PublishSubject.create<Unit>()
    val intentDeleteAccount = PublishSubject.create<Account>()
    val accountsTotal = accounts
        .map { it.fold(BigDecimal.ZERO) { acc, account -> acc + account.amount } }
        .toBehaviorSubject()
    init {
        disposables.addAll(
            intentAddAccount
                .subscribe {
                    viewModelScope.launch {
                        repo.addAccount(Account("", "0.00".toBigDecimal()))
                    }
                },
            intentDeleteAccount
                .subscribe {
                    viewModelScope.launch {
                        repo.deleteAccount(it)
                    }
                }
        )
    }

    fun updateAccount(account: Account) {
        viewModelScope.launch {
            if (account != repo.getAccount(account.id))
                repo.updateAccount(account)
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}