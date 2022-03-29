package com.tminus1010.budgetvalue._unrestructured.transactions.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._unrestructured.transactions.app.ReceiptCategorizationInteractor
import com.tminus1010.budgetvalue.domain.Category
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.*
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
    val categoryAmountsTableView =
        receiptCategorizationInteractor.categoryAmounts.flow.map {
            TableViewVMItem(
                recipeGrid = listOf(
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
                                menuVMItems = MenuVMItems(
                                    MenuVMItem(
                                        title = "Remove",
                                        onClick = { remove(category, amount) },
                                    )
                                )
                            ),
                            TextVMItem(
                                text1 = category.name,
                                menuVMItems = MenuVMItems(
                                    MenuVMItem(
                                        title = "Remove",
                                        onClick = { remove(category, amount) },
                                    )
                                )
                            ),
                        )
                    },
                ).flatten(),
                shouldFitItemWidthsInsideTable = true,
                rowFreezeCount = 1,
            )
        }
}
