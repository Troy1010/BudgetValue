package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.domain.TransactionsAppService
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import java.io.InputStream
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class TransactionsMiscVM @Inject constructor(
    private val transactionsAppService: TransactionsAppService,
) : ViewModel() {
    // # Input
    fun userImportTransactions(inputStream: InputStream) {
        transactionsAppService.importTransactions(inputStream)
            .subscribe()
    }

    // # Output
    val currentSpendBlockCAs: Observable<Map<Category, BigDecimal>> =
        transactionsAppService.currentSpendBlockCAs
    val uncategorizedSpendsSize: Observable<String> =
        transactionsAppService.uncategorizedSpends
            .map { it.size.toString() }
}