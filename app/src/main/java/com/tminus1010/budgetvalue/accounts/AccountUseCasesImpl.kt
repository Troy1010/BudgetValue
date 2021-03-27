package com.tminus1010.budgetvalue.accounts

import com.tminus1010.budgetvalue.aa_core.data.Repo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class AccountUseCasesImpl @Inject constructor(
    private val repo: Repo
) : AccountUseCases {
    override fun fetchAccounts(): Observable<List<Account>> =
        repo.fetchAccounts()
            .map { it.map { Account.fromDTO(it) } }

    override fun update(account: Account): Completable =
        repo.updateAccount(account.toDTO())

    override fun push(account: Account): Completable =
        repo.addAccount(account.toDTO())

    override fun delete(account: Account): Completable =
        repo.deleteAccount(account.toDTO())
}