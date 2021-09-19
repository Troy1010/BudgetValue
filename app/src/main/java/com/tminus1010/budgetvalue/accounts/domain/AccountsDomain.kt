package com.tminus1010.budgetvalue.accounts.domain

import com.tminus1010.budgetvalue.all.data.repos.AccountsRepo
import com.tminus1010.tmcommonkotlin.misc.extensions.sum
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountsDomain @Inject constructor(
    accountsRepo: AccountsRepo
) {
    val accountsTotal: Observable<BigDecimal> =
        accountsRepo.accounts
            .map { it.map { it.amount }.sum() }
            .replay(1).refCount()
}