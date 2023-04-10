package com.tminus1010.buva.ui.all_features.view_model_item

import com.tminus1010.buva.app.ReconciliationSkipInteractor
import com.tminus1010.buva.data.CurrentDatePeriod
import com.tminus1010.buva.domain.TransactionBlock
import com.tminus1010.buva.ui.review.history.HistoryPresentationModel
import com.tminus1010.tmcommonkotlin.customviews.IHasToViewItemRecipe

class TransactionBlockCompletionPresentationModel(transactionBlock: TransactionBlock, currentDatePeriod: CurrentDatePeriod, shouldSkip: Boolean, reconciliationSkipInteractor: ReconciliationSkipInteractor) {
    val transactionTitle = HistoryPresentationModel.TransactionBlockPresentationModel(transactionBlock, null, currentDatePeriod).subTitle
    val transactionCompletionPercentage = transactionBlock.percentageOfCategorizedTransactions

    fun toPresentationModels(): List<IHasToViewItemRecipe> {
        return listOf(
            TextVMItem(text4 = transactionTitle),
            TextPresentationModel(text1 = "${(transactionCompletionPercentage * 100).toInt()}%"),
        )
    }
}