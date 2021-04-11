package com.tminus1010.budgetvalue.accounts.domain

import com.tminus1010.budgetvalue.accounts.data.IAccountsRepo
import com.tminus1010.budgetvalue.accounts.models.Account
import com.tminus1010.budgetvalue._core.extensions.launch
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountsDomain @Inject constructor(
    accountsRepo: IAccountsRepo
) {
    val accounts: Observable<List<Account>> = accountsRepo.fetchAccounts()
        .replay(1).refCount()

    val accountsTotal: Observable<BigDecimal> = accounts
        .map { it.map { it.amount }.sum() }
        .replay(1).refCount()
}