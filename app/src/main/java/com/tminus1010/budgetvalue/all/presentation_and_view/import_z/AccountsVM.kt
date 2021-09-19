package com.tminus1010.budgetvalue.all.presentation_and_view.import_z

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue.all.data.repos.AccountsRepo
import com.tminus1010.budgetvalue.accounts.domain.AccountsDomain
import com.tminus1010.budgetvalue.all.domain.models.Account
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.toState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.Subject
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class AccountsVM @Inject constructor(
    private val accountsRepo: AccountsRepo,
    accountsDomain: AccountsDomain,
    errorSubject: Subject<Throwable>,
) : ViewModel() {
    // # Input
    fun userAddAccount() {
        accountsRepo.add(Account("", BigDecimal.ZERO)).observe(disposables)
    }

    fun userDeleteAccount(account: Account) {
        accountsRepo.delete(account).observe(disposables)
    }

    fun userUpdateAccount(account: Account) {
        accountsRepo.update(account).observe(disposables)
    }

    // # Output
    val accounts = accountsDomain.accounts
        .toState(disposables, errorSubject)
    val accountsTotal = accountsDomain.accountsTotal
        .map { it.toString() }
        .toState(disposables, errorSubject)
}