package com.tminus1010.buva.ui.receipt_categorization

import androidx.lifecycle.ViewModel
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.ui.all_features.view_model_item.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ReceiptCategorizationSoFarVM @Inject constructor(
    private val receiptCategorizationSharedVM: ReceiptCategorizationSharedVM,
) : ViewModel() {
    fun remove(category: Category, amount: BigDecimal) {
        receiptCategorizationSharedVM.categoryAmounts.remove(Pair(category, amount))
    }

    // # State
    val categoryAmountsTableView =
        receiptCategorizationSharedVM.categoryAmounts.flow.map {
            TableViewVMItem(
                recipeGrid = listOf(
                    listOf(
                        listOf(
                            HeaderPresentationModel("Amount"),
                            HeaderPresentationModel("Category"),
                        ),
                    ),
                    it.map { (category, amount) ->
                        val menuVMItems =
                            MenuVMItems(
                                MenuVMItem(
                                    title = "Remove",
                                    onClick = { remove(category, amount) },
                                )
                            )
                        listOf(
                            TextVMItem(text1 = category.name, menuVMItems = menuVMItems),
                            TextVMItem(text1 = amount.toPlainString(), menuVMItems = menuVMItems),
                        )
                    },
                ).flatten(),
                shouldFitItemWidthsInsideTable = true,
                rowFreezeCount = 1,
            )
        }
}
