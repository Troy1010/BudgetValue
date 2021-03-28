package com.tminus1010.budgetvalue.accounts.domain

import com.tminus1010.budgetvalue._layer_facades.DomainFacade
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import dagger.Reusable
import java.math.BigDecimal
import javax.inject.Inject

@Reusable
class AccountsDomain @Inject constructor(
    domainFacade: DomainFacade
) : IAccountsDomain {
    override val accounts = domainFacade.fetchAccounts().toBehaviorSubject(emptyList())
    override val accountsTotal = accounts
        .map { it.fold(BigDecimal.ZERO) { acc, account -> acc + account.amount } }
        .toBehaviorSubject()
}