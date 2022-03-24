package com.tminus1010.budgetvalue.transactions.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.easyEmit
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.isZero
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue.all_features.ui.all_features.model.ButtonVMItem
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.all_features.domain.Category
import com.tminus1010.budgetvalue.transactions.app.ReceiptCategorizationInteractor
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
