package com.tminus1010.budgetvalue.all.data.repos

import com.tminus1010.budgetvalue._core.data.MiscDAO
import com.tminus1010.budgetvalue.all.domain.models.Account
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class AccountsRepo @Inject constructor(
    private val miscDAO: MiscDAO
) {
    fun fetchAccounts(): Observable<List<Account>> =
        miscDAO.fetchAccounts().subscribeOn(Schedulers.io())
            .map { it.map { Account.fromDTO(it) } }

    private fun getAccount(id: Int): Observable<Account> =
        miscDAO.getAccount(id).subscribeOn(Schedulers.io())
            .map { Account.fromDTO(it) }

    fun update(account: Account): Completable =
        getAccount(account.id)
            .take(1)
            .flatMapCompletable {
                if (it == account)
                    Completable.complete()
                else
                    miscDAO.update(account.toDTO()).subscribeOn(Schedulers.io())
            }

    fun add(account: Account): Completable =
        miscDAO.addAccount(account.toDTO()).subscribeOn(Schedulers.io())

    fun delete(account: Account): Completable =
        miscDAO.deleteAccount(account.toDTO()).subscribeOn(Schedulers.io())
}