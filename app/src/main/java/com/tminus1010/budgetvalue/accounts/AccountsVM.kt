package com.tminus1010.budgetvalue.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue.accounts.data.IAccountsRepo
import com.tminus1010.budgetvalue.accounts.domain.AccountsDomain
import com.tminus1010.budgetvalue.accounts.models.Account
import com.tminus1010.tmcommonkotlin.rx.extensions.launch
import com.tminus1010.tmcommonkotlin.rx.toState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.Subject
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class AccountsVM @Inject constructor(
    private val accountsRepo: IAccountsRepo,
    accountsDomain: AccountsDomain,
    errorSubject: Subject<Throwable>,
) : ViewModel() {
    // # State
    val accounts = accountsDomain.accounts
        .startWithItem(emptyList())
        .toState(disposables, errorSubject)
    val accountsTotal = accountsDomain.accountsTotal
        .map { it.toString() }
        .toState(disposables, errorSubject)
    // # Intents
    fun addAccount() {
        accountsRepo.push(Account("", BigDecimal.ZERO)).launch()
    }

    fun deleteAccount(account: Account) {
        accountsRepo.delete(account).launch()
    }

    fun updateAccount(account: Account) {
        accountsRepo.update(account).launch()
    }
}