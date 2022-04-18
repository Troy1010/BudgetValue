package com.tminus1010.buva.app.model

data class ImportTransactionsResult(
    val numberOfTransactionsImported: Int,
    val numberOfTransactionsIgnoredBecauseTheyWereAlreadyImported: Int,
    val numberOfTransactionsCategorizedByFutures: Int,
)