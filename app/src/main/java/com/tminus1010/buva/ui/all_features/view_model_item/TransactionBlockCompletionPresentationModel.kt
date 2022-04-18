package com.tminus1010.buva.ui.all_features.view_model_item

import com.tminus1010.buva.data.CurrentDatePeriod
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IHasToViewItemRecipe
import com.tminus1010.buva.ui.history.HistoryPresentationModel
import com.tminus1010.buva.domain.TransactionBlock

class TransactionBlockCompletionPresentationModel(transactionBlock: TransactionBlock, currentDatePeriod: CurrentDatePeriod) {
    val transactionTitle = HistoryPresentationModel.TransactionBlockPresentationModel(transactionBlock, currentDatePeriod).subTitle
    val transactionCompletionPercentage = transactionBlock.percentageOfCategorizedTransactions

    fun toPresentationModels(): List<IHasToViewItemRecipe> {
        return listOf(
            TextVMItem(text4 = transactionTitle),
            TextPresentationModel(text1 = "${(transactionCompletionPercentage * 100).toInt()}%"),
        )
    }
}