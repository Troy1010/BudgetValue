package com.tminus1010.budgetvalue.app.model

data class ImportTransactionsResult(
    val numberOfTransactionsIgnoredBecauseTheyWereAlreadyImported: Int,
    val numberOfTransactionsImported: Int,
    val numberOfTransactionsCategorizedByFutures: Int,
)