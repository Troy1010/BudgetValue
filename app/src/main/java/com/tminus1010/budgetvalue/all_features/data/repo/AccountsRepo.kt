package com.tminus1010.budgetvalue.all_features.data.repo

import com.tminus1010.budgetvalue.all_features.data.MiscDAO
import com.tminus1010.budgetvalue.all_features.domain.accounts.Account
import com.tminus1010.budgetvalue.all_features.domain.accounts.AccountsAggregate
import com.tminus1010.tmcommonkotlin.rx.replayNonError
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountsRepo @Inject constructor(
    private val miscDAO: MiscDAO,
) {
    val accountsAggregate =
        miscDAO.fetchAccounts().subscribeOn(Schedulers.io())
            .map(::AccountsAggregate)
            .replayNonError(1)

    private fun getAccount(id: Int): Observable<Account> =
        miscDAO.getAccount(id).subscribeOn(Schedulers.io())

    fun update(account: Account): Completable =
        getAccount(account.id)
            .take(1)
            .flatMapCompletable {
                if (it == account)
                    Completable.complete()
                else
                    miscDAO.update(account).subscribeOn(Schedulers.io())
            }

    fun add(account: Account): Completable =
        miscDAO.addAccount(account).subscribeOn(Schedulers.io())

    fun delete(account: Account): Completable =
        miscDAO.deleteAccount(account).subscribeOn(Schedulers.io())
}