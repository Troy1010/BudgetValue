package com.tminus1010.budgetvalue.accounts.data

import com.tminus1010.budgetvalue.accounts.models.Account
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface IAccountsRepo {
    fun fetchAccounts(): Observable<List<Account>>
    fun getAccount(id: Int): Observable<Account>
    fun add(account: Account): Completable
    fun update(account: Account): Completable
    fun delete(account: Account): Completable
}