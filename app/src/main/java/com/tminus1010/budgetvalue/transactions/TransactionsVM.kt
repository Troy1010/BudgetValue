package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.extensions.divertErrors
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.Subject
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class TransactionsVM @Inject constructor(
    errorSubject: Subject<Throwable>,
    private val transactionsDomain: TransactionsDomain,
) : ViewModel() {
    val currentSpendBlockCAs = transactionsDomain.currentSpendBlockCAs
    val uncategorizedSpendsSize = transactionsDomain.uncategorizedSpendsSize
        .map { it.toString() }
        .divertErrors(errorSubject)
    fun importTransactions(inputStream: InputStream) =
        transactionsDomain.importTransactions(inputStream)
}