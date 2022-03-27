package com.tminus1010.budgetvalue.app.model

data class ImportTransactionsResult(
    val numberOfTransactionsImported: Int,
    val numberOfTransactionsIgnoredBecauseTheyWereAlreadyImported: Int,
    val numberOfTransactionsCategorizedByFutures: Int,
)