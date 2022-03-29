package com.tminus1010.budgetvalue._unrestructured.transactions.presentation.model

import com.tminus1010.budgetvalue.data.CurrentDatePeriodRepo
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.TextPresentationModel
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.TextVMItem
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IHasToViewItemRecipe
import com.tminus1010.budgetvalue._unrestructured.history.HistoryVMItem
import com.tminus1010.budgetvalue._unrestructured.transactions.app.TransactionBlock

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