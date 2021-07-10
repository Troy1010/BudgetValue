package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.extensions.divertErrors
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.Subject
import java.io.InputStream
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class TransactionsVM @Inject constructor(
    errorSubject: Subject<Throwable>,
    private val transactionsDomain: TransactionsDomain,
) : ViewModel() {
    // # Input
    fun importTransactions(inputStream: InputStream) {
        transactionsDomain.importTransactions(inputStream)
            .observe(disposables)
    }

    // # Output
    val currentSpendBlockCAs: Observable<Map<Category, BigDecimal>> =
        transactionsDomain.currentSpendBlockCAs
    val uncategorizedSpendsSize =
        transactionsDomain.uncategorizedSpendsSize
            .map { it.toString() }
            .divertErrors(errorSubject)
}