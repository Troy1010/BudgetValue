package com.tminus1010.budgetvalue.all.domain.models

import com.tminus1010.tmcommonkotlin.misc.extensions.sum

class Accounts(val accounts: List<Account>) {
    val total = accounts.map { it.amount }.sum()
}