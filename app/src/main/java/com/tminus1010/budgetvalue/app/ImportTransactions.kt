package com.tminus1010.budgetvalue.app

import android.app.Application
import android.net.Uri
import com.tminus1010.budgetvalue.all_layers.extensions.value
import com.tminus1010.budgetvalue.app.model.ImportTransactionsResult
import com.tminus1010.budgetvalue.app.model.RedoUndo
import com.tminus1010.budgetvalue.data.FuturesRepo
import com.tminus1010.budgetvalue.data.TransactionsRepo
import com.tminus1010.budgetvalue.data.service.TransactionInputStreamAdapter
import com.tminus1010.budgetvalue.domain.TerminationStrategy
import com.tminus1010.budgetvalue.domain.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.time.LocalDate
import javax.inject.Inject

class ImportTransactions @Inject constructor(
    private val app: Application,
    private val transactionInputStreamAdapter: TransactionInputStreamAdapter,
    private val futuresRepo: FuturesRepo,
    private val categoriesInteractor: CategoriesInteractor,
    private val transactionsRepo: TransactionsRepo,
    private val transactionsInteractor: TransactionsInteractor
) {
    suspend operator fun invoke(uri: Uri) = withContext(Dispatchers.IO) {
        invoke(
            transactionInputStreamAdapter.parseToTransactions(
                app.contentResolver.openInputStream(uri) ?: error("Could not find data at uri:$uri")
            )
        )
    }

    suspend operator fun invoke(inputStream: InputStream) =
        invoke(transactionInputStreamAdapter.parseToTransactions(inputStream))

    suspend operator fun invoke(transactions: Iterable<Transaction>): ImportTransactionsResult {
        var transactionsImportedCounter: Int
        var transactionsCategorizedCounter = 0
        var transactionsIgnoredBecauseTheyWereAlreadyImportedCounter = 0
        transactions
            .filter { (transactionsRepo.getTransaction2(it.id) == null).also { if (!it) transactionsIgnoredBecauseTheyWereAlreadyImportedCounter++ } }
            .map { transaction ->
                val matchedCategorization =
                    categoriesInteractor.userCategories.value!!
                        .find { it.onImportTransactionMatcher?.isMatch(transaction) ?: false }
                        ?: futuresRepo.futures.value!!
                            .find { it.onImportTransactionMatcher?.isMatch(transaction) ?: false }
                            ?.also { if (it.terminationStrategy == TerminationStrategy.ONCE) futuresRepo.setTerminationDate(it, LocalDate.now()) }
                matchedCategorization?.categorize(transaction)
                    ?.also { transactionsCategorizedCounter++ }
                    ?: transaction
            }
            .also { transactionsInteractor.push(it.also { transactionsImportedCounter = it.size }) }
        return ImportTransactionsResult(
            numberOfTransactionsImported = transactionsImportedCounter,
            numberOfTransactionsCategorizedByFutures = transactionsCategorizedCounter,
            numberOfTransactionsIgnoredBecauseTheyWereAlreadyImported = transactionsIgnoredBecauseTheyWereAlreadyImportedCounter
        )
    }
}