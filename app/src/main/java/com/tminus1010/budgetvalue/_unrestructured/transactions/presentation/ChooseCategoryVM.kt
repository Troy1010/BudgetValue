package com.tminus1010.budgetvalue._unrestructured.transactions.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.all_layers.extensions.easyEmit
import com.tminus1010.budgetvalue.all_layers.extensions.isZero
import com.tminus1010.budgetvalue.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.budgetvalue.app.CategoriesInteractor
import com.tminus1010.budgetvalue.domain.Category
import com.tminus1010.budgetvalue._unrestructured.transactions.app.ReceiptCategorizationInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ChooseCategoryVM @Inject constructor(
    categoriesInteractor: CategoriesInteractor,
    private val receiptCategorizationInteractor: ReceiptCategorizationInteractor,
) : ViewModel() {
    // # UserIntents
    fun userSubmitPartialCategorization(category: Category) {
        receiptCategorizationInteractor.submitPartialCategorization(category)
        navUp.easyEmit(Unit)
    }

    // # State
    val partialAmountToCategorize = receiptCategorizationInteractor.rememberedAmount.map { if (it.isZero) null else it.toString().toMoneyBigDecimal().toString() }
    val categoryButtonVMItems =
        categoriesInteractor.userCategories
            .map {
                it.map { category ->
                    ButtonVMItem(
                        title = category.name,
                        onClick = { userSubmitPartialCategorization(category) },
                    )
                }
            }

    // # Presentation Events
    val navUp = MutableSharedFlow<Unit>()
}
