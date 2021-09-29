package com.tminus1010.budgetvalue.transactions.presentation.models

import com.tminus1010.budgetvalue.transactions.domain.models.TransactionListDomainModel
import io.reactivex.rxjava3.core.Observable

class TransactionVMItemList(
    private val transactionListDomainModel: TransactionListDomainModel
) : List<TransactionVMItem> by transactionListDomainModel.transactions.map(::TransactionVMItem) {
    val navToTransaction = Observable.merge(this.map { it.userTryNavToTransaction })
}