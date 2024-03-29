package com.tminus1010.buva.domain

import com.tminus1010.tmcommonkotlin.misc.extensions.sum

class AccountsAggregate(val accounts: List<Account>) {
    val total = accounts.map { it.amount }.sum()
}