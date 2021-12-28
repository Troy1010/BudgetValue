package com.tminus1010.budgetvalue.transactions.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.presentation.model.MenuPresentationModel
import com.tminus1010.budgetvalue._core.presentation.model.MenuVMItem
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.reconcile.presentation.model.HeaderPresentationModel
import com.tminus1010.budgetvalue.transactions.app.ReceiptCategorizationInteractor
import com.tminus1010.budgetvalue.transactions.presentation.models.TextPresentationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ReceiptCategorizationSoFarVM @Inject constructor(
    private val receiptCategorizationInteractor: ReceiptCategorizationInteractor,
) : ViewModel() {
    // # User Intents
    fun userRemove(category: Category) {
        receiptCategorizationInteractor.categoryAmounts.remove(category)
    }

    // # Presentation State
    val recipeGrid =
        receiptCategorizationInteractor.categoryAmounts.flow.map {
            listOf(
                listOf(
                    listOf(
                        HeaderPresentationModel("Category"),
                        HeaderPresentationModel("Amount"),
                    ),
                ),
                it.map { (category, amount) ->
                    listOf(
                        TextPresentationModel(
                            text1 = category.name,
                            menuPresentationModel = MenuPresentationModel(
                                MenuVMItem(
                                    title = "Remove",
                                    onClick = { userRemove(category) },
                                )
                            )
                        ),
                        TextPresentationModel(
                            text1 = amount.toPlainString(),
                            menuPresentationModel = MenuPresentationModel(
                                MenuVMItem(
                                    title = "Remove",
                                    onClick = { userRemove(category) },
                                )
                            )
                        ),
                    )
                },
            ).flatten()
        }
}
