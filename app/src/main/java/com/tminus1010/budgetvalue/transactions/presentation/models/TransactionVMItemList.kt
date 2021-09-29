package com.tminus1010.budgetvalue.transactions.presentation.models

import com.tminus1010.budgetvalue.transactions.domain.models.TransactionsDomainModel
import io.reactivex.rxjava3.core.Observable

class TransactionVMItemList(
    private val transactionsDomainModel: TransactionsDomainModel
) : List<TransactionVMItem> by transactionsDomainModel.transactionsSortedByDate.map(::TransactionVMItem) {
    val navToTransaction = Observable.merge(this.map { it.userTryNavToTransaction })
}