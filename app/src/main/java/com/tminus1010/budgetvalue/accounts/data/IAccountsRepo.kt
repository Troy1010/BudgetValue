package com.tminus1010.budgetvalue.accounts.data

import com.tminus1010.budgetvalue.accounts.models.Account
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface IAccountsRepo {
    fun fetchAccounts(): Observable<List<Account>>
    fun push(account: Account): Completable
    fun update(account: Account): Completable
    fun delete(account: Account): Completable
}