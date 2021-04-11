package com.tminus1010.budgetvalue.accounts.domain

import com.tminus1010.budgetvalue.accounts.data.IAccountsRepo
import com.tminus1010.budgetvalue.accounts.models.Account
import com.tminus1010.budgetvalue._core.extensions.launch
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountsDomain @Inject constructor(
    accountsRepo: IAccountsRepo
) {
    val accounts = accountsRepo.fetchAccounts()
        .replay(1).refCount()

    val accountsTotal = accounts
        .map { it.fold(BigDecimal.ZERO) { acc, account -> acc + account.amount } }
        .replay(1).refCount()
}