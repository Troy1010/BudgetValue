package com.tminus1010.budgetvalue.accounts

import com.tminus1010.budgetvalue._core.data.RepoFacade
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class AccountUseCasesImpl @Inject constructor(
    private val repoFacade: RepoFacade
) : AccountUseCases {
    override fun fetchAccounts(): Observable<List<Account>> =
        repoFacade.fetchAccounts()
            .map { it.map { Account.fromDTO(it) } }

    override fun update(account: Account): Completable =
        repoFacade.updateAccount(account.toDTO())

    override fun push(account: Account): Completable =
        repoFacade.addAccount(account.toDTO())

    override fun delete(account: Account): Completable =
        repoFacade.deleteAccount(account.toDTO())
}