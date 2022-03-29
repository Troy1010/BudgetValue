package com.tminus1010.budgetvalue.ui.all_features.view_model_item

import com.tminus1010.budgetvalue.data.CurrentDatePeriodRepo
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IHasToViewItemRecipe
import com.tminus1010.budgetvalue.ui.history.HistoryPresentationModel
import com.tminus1010.budgetvalue._unrestructured.transactions.app.TransactionBlock

class TransactionBlockCompletionPresentationModel(transactionBlock: TransactionBlock, currentDatePeriodRepo: CurrentDatePeriodRepo) {
    val transactionTitle = HistoryPresentationModel.TransactionBlockPresentationModel(transactionBlock, currentDatePeriodRepo).subTitle
    val transactionCompletionPercentage = transactionBlock.percentageOfCategorizedTransactions

    fun toPresentationModels(): List<IHasToViewItemRecipe> {
        return listOf(
            TextVMItem(text2 = transactionTitle),
            TextPresentationModel(text1 = "${(transactionCompletionPercentage * 100).toInt()}%"),
        )
    }
}