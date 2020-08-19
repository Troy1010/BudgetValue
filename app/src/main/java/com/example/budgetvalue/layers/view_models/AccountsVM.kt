package com.example.budgetvalue.layers.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetvalue.layers.data_layer.Repo
import com.example.budgetvalue.models.Account
import com.example.tmcommonkotlin.logz
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.launch

class AccountsVM(private val repo: Repo): ViewModel() {
    val disposables = CompositeDisposable()
    val accounts = repo.getAccounts()
    val intentAddAccount = PublishSubject.create<Unit>()
    val intentDeleteAccount = PublishSubject.create<Account>()
    init {
        disposables.addAll(
            intentAddAccount
                .subscribe {
                    viewModelScope.launch {
                        repo.addAccount(Account("", "0.00"))
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
            repo.updateAccount(account)
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}