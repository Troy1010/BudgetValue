package com.tminus1010.budgetvalue.all.data.repos

import com.tminus1010.budgetvalue._core.data.MiscDAO
import com.tminus1010.budgetvalue.all.domain.models.Account
import com.tminus1010.budgetvalue.all.domain.models.AccountsAggregate
import com.tminus1010.tmcommonkotlin.rx.replayNonError
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountsRepo @Inject constructor(
    private val miscDAO: MiscDAO
) {
    val accountsAggregate =
        miscDAO.fetchAccounts().subscribeOn(Schedulers.io())
            .map { it.map(Account::fromDTO) }
            .map(::AccountsAggregate)
            .replayNonError(1)

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