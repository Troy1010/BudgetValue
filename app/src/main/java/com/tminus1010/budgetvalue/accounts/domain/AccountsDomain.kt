package com.tminus1010.budgetvalue.accounts.domain

import com.tminus1010.budgetvalue.accounts.data.IAccountsRepo
import com.tminus1010.budgetvalue.accounts.models.Account
import com.tminus1010.budgetvalue._core.extensions.launch
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountsDomain @Inject constructor(
    accountsRepo: IAccountsRepo
) : IAccountsDomain {
    override val intentAddAccount = PublishSubject.create<Unit>()
        .also { it.launch { accountsRepo.push(Account("", BigDecimal.ZERO)) } }
    override val intentDeleteAccount = PublishSubject.create<Account>()
        .also { it.launch { accountsRepo.delete(it) } }
    override val intentUpdateAccount = PublishSubject.create<Account>()
        .also { it.launch { accountsRepo.update(it) } }
    override val accounts = accountsRepo.fetchAccounts().toBehaviorSubject(emptyList())
    override val accountsTotal = accounts
        .map { it.fold(BigDecimal.ZERO) { acc, account -> acc + account.amount } }
        .toBehaviorSubject()
}