package com.tminus1010.budgetvalue.transactions.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.reconcile.presentation.model.HeaderPresentationModel
import com.tminus1010.budgetvalue.transactions.app.ReceiptCategorizationInteractor
import com.tminus1010.budgetvalue.transactions.presentation.models.TextPresentationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ReceiptCategorizationSoFarVM @Inject constructor(
    receiptCategorizationInteractor: ReceiptCategorizationInteractor,
) : ViewModel() {
    // # Presentation State
    val recipeGrid =
        receiptCategorizationInteractor.categoryAmountsFlow.map {
            listOf(
                listOf(
                    listOf(
                        HeaderPresentationModel("Category"),
                        HeaderPresentationModel("Amount"),
                    ),
                ),
                it.map { (category, amount) ->
                    listOf(
                        TextPresentationModel(category.name),
                        TextPresentationModel(amount.toPlainString()),
                    )
                },
            ).flatten()
        }
}
