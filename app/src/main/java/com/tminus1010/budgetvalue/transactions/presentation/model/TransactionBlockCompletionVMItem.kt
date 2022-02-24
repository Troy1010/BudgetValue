package com.tminus1010.budgetvalue.transactions.presentation.model

import com.tminus1010.budgetvalue._core.data.repo.CurrentDatePeriodRepo
import com.tminus1010.budgetvalue.budgeted.presentation.IHasToViewItemRecipe
import com.tminus1010.budgetvalue.history.HistoryVMItem
import com.tminus1010.budgetvalue._core.presentation.model.TextPresentationModel
import com.tminus1010.budgetvalue.transactions.app.TransactionBlock

class TransactionBlockCompletionVMItem(transactionBlock: TransactionBlock, currentDatePeriodRepo: CurrentDatePeriodRepo) {
    val transactionTitle = HistoryVMItem.TransactionBlockVMItem(transactionBlock, currentDatePeriodRepo).subTitle
    val transactionCompletionPercentage = transactionBlock.percentageOfCategorizedTransactions

    fun toPresentationModels(): List<IHasToViewItemRecipe> {
        return listOf(
            ObservableTextPresentationModel(transactionTitle),
            TextPresentationModel(text = "${(transactionCompletionPercentage*100).toInt()}%"),
        )
    }
}