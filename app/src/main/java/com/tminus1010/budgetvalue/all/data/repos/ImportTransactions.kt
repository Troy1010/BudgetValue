package com.tminus1010.budgetvalue.all.data.repos

import android.app.Application
import android.net.Uri
import com.tminus1010.budgetvalue.transactions.app.TransactionsInteractor
import io.reactivex.rxjava3.core.Completable
import java.io.InputStream
import javax.inject.Inject

class ImportTransactions @Inject constructor(
    private val app: Application,
    private val transactionsInteractor: TransactionsInteractor,
) {
    operator fun invoke(uri: Uri): Completable {
        return invoke(
            app.contentResolver
                .openInputStream(uri)
                ?: error("Could not find data at uri:$uri")
        )
    }

    operator fun invoke(inputStream: InputStream): Completable {
        return transactionsInteractor.importTransactions(inputStream)
    }
}