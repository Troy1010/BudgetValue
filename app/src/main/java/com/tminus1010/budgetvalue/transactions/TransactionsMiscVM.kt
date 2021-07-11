package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import java.io.InputStream
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class TransactionsMiscVM @Inject constructor(
    private val transactionsDomain: TransactionsDomain,
) : ViewModel() {
    // # Input
    fun userImportTransactions(inputStream: InputStream) {
        transactionsDomain.importTransactions(inputStream)
            .subscribe()
    }

    // # Output
    val currentSpendBlockCAs: Observable<Map<Category, BigDecimal>> =
        transactionsDomain.currentSpendBlockCAs
    val uncategorizedSpendsSize: Observable<String> =
        transactionsDomain.uncategorizedSpends
            .map { it.size.toString() }
}