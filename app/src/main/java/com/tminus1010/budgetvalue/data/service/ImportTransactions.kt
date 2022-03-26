package com.tminus1010.budgetvalue.data.service

import android.app.Application
import android.net.Uri
import com.tminus1010.budgetvalue._unrestructured.transactions.app.interactor.TransactionsInteractor
import java.io.InputStream
import javax.inject.Inject

class ImportTransactions @Inject constructor(
    private val app: Application,
    private val transactionsInteractor: TransactionsInteractor,
) {
    suspend operator fun invoke(uri: Uri) {
        invoke(
            app.contentResolver
                .openInputStream(uri)
                ?: error("Could not find data at uri:$uri")
        )
    }

    suspend operator fun invoke(inputStream: InputStream) {
        return transactionsInteractor.importTransactions(inputStream)
    }
}