package com.tminus1010.buva.ui.all_features

import com.tminus1010.buva.app.model.ImportTransactionsResult
import com.tminus1010.tmcommonkotlin.androidx.ShowAlertDialog

class ShowImportResultAlertDialog constructor(private val showAlertDialog: ShowAlertDialog) {
    suspend operator fun invoke(importTransactionsResult: ImportTransactionsResult) {
        showAlertDialog(
            """
                Import Successful
                ${importTransactionsResult.numberOfTransactionsImported} imported
                ${importTransactionsResult.numberOfTransactionsIgnoredBecauseTheyWereAlreadyImported} ignored because they were already imported
                ${importTransactionsResult.numberOfTransactionsCategorizedByFutures} categorized by futures
            """.trimIndent()
        )
    }
}