package com.tminus1010.budgetvalue.transactions.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.all_features.presentation.model.MenuPresentationModel
import com.tminus1010.budgetvalue.all_features.presentation.model.MenuVMItem
import com.tminus1010.budgetvalue.all_features.app.model.Category
import com.tminus1010.budgetvalue.reconcile.presentation.model.HeaderPresentationModel
import com.tminus1010.budgetvalue.transactions.app.ReceiptCategorizationInteractor
import com.tminus1010.budgetvalue.all_features.presentation.model.TextVMItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ReceiptCategorizationSoFarVM @Inject constructor(
    private val receiptCategorizationInteractor: ReceiptCategorizationInteractor,
) : ViewModel() {
    fun remove(category: Category, amount: BigDecimal) {
        receiptCategorizationInteractor.categoryAmounts.remove(Pair(category, amount))
    }

    // # State
    val recipeGrid =
        receiptCategorizationInteractor.categoryAmounts.flow.map {
            listOf(
                listOf(
                    listOf(
                        HeaderPresentationModel("Amount"),
                        HeaderPresentationModel("Category"),
                    ),
                ),
                it.map { (category, amount) ->
                    listOf(
                        TextVMItem(
                            text1 = amount.toPlainString(),
                            menuPresentationModel = MenuPresentationModel(
                                MenuVMItem(
                                    title = "Remove",
                                    onClick = { remove(category, amount) },
                                )
                            )
                        ),
                        TextVMItem(
                            text1 = category.name,
                            menuPresentationModel = MenuPresentationModel(
                                MenuVMItem(
                                    title = "Remove",
                                    onClick = { remove(category, amount) },
                                )
                            )
                        ),
                    )
                },
            ).flatten()
        }
}
