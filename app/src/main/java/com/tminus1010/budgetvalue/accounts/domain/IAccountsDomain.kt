package com.tminus1010.budgetvalue.accounts.domain

import com.tminus1010.budgetvalue.accounts.models.Account
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal

interface IAccountsDomain {
    val accounts: BehaviorSubject<List<Account>>
    val accountsTotal: BehaviorSubject<BigDecimal>
}