package com.tminus1010.budgetvalue.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue.accounts.data.IAccountsRepo
import com.tminus1010.budgetvalue.accounts.domain.AccountsDomain
import com.tminus1010.budgetvalue.accounts.models.Account
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.toState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.subjects.Subject
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class AccountsVM @Inject constructor(
    private val accountsRepo: IAccountsRepo,
    accountsDomain: AccountsDomain,
    errorSubject: Subject<Throwable>,
) : ViewModel() {
    // # Input
    fun addAccount() {
        accountsRepo.add(Account("", BigDecimal.ZERO)).observe(disposables)
    }

    fun deleteAccount(account: Account) {
        accountsRepo.delete(account).observe(disposables)
    }

    fun updateAccount(account: Account) {
        accountsRepo.getAccount(account.id)
            .take(1)
            .flatMapCompletable {
                if (it == account)
                    Completable.complete()
                else
                    accountsRepo.update(account)
            }.observe(disposables)
    }

    // # Output
    val accounts = accountsDomain.accounts
        .startWithItem(emptyList())
        .toState(disposables, errorSubject)
    val accountsTotal = accountsDomain.accountsTotal
        .map { it.toString() }
        .toState(disposables, errorSubject)
}