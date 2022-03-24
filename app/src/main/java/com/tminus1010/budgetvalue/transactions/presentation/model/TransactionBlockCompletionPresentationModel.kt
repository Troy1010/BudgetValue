package com.tminus1010.budgetvalue.transactions.presentation.model

import com.tminus1010.budgetvalue.all_features.data.CurrentDatePeriodRepo
import com.tminus1010.budgetvalue.all_features.ui.all_features.model.TextPresentationModel
import com.tminus1010.budgetvalue.all_features.ui.all_features.model.TextVMItem
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IHasToViewItemRecipe
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