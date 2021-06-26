package com.tminus1010.budgetvalue.transactions.domain

import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.models.Transaction
import com.tminus1010.tmcommonkotlin.tuple.Box
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

interface ICategorizeTransactionsDomain {
    val transactionBox: Observable<Box<Transaction?>>
    val hasUncategorizedTransaction: Observable<Boolean>
    fun finishTransactionWithCategory(category: Category)
}