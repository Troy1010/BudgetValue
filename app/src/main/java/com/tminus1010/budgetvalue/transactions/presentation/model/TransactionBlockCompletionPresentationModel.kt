package com.tminus1010.budgetvalue.transactions.presentation.model

import com.tminus1010.budgetvalue.all_features.data.repo.CurrentDatePeriodRepo
import com.tminus1010.budgetvalue.all_features.presentation.model.TextPresentationModel
import com.tminus1010.budgetvalue.all_features.presentation.model.TextVMItem
import com.tminus1010.budgetvalue.budgeted.presentation.IHasToViewItemRecipe
import com.tminus1010.budgetvalue.history.HistoryVMItem
import com.tminus1010.budgetvalue.transactions.app.TransactionBlock

class TransactionBlockCompletionPresentationModel(transactionBlock: TransactionBlock, currentDatePeriodRepo: CurrentDatePeriodRepo) {
    val transactionTitle = HistoryVMItem.TransactionBlockVMItem(transactionBlock, currentDatePeriodRepo).subTitle
    val transactionCompletionPercentage = transactionBlock.percentageOfCategorizedTransactions

    fun toPresentationModels(): List<IHasToViewItemRecipe> {
        return listOf(
            TextVMItem(text2 = transactionTitle),
            TextPresentationModel(text1 = "${(transactionCompletionPercentage * 100).toInt()}%"),
        )
    }
}