package com.tminus1010.budgetvalue.layer_domain.use_cases

import com.tminus1010.budgetvalue.model_domain.Account
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface AccountUseCases {
    val accounts: Observable<List<Account>>
    fun push(account: Account): Completable
    fun update(account: Account): Completable
    fun delete(account: Account): Completable
}