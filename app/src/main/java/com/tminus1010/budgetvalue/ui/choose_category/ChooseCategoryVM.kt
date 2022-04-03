package com.tminus1010.budgetvalue.ui.choose_category

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.all_layers.extensions.easyEmit
import com.tminus1010.budgetvalue.all_layers.extensions.isZero
import com.tminus1010.budgetvalue.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue.app.UserCategories
import com.tminus1010.budgetvalue.domain.Category
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.budgetvalue.ui.receipt_categorization.ReceiptCategorizationSharedVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ChooseCategoryVM @Inject constructor(
    userCategories: UserCategories,
    private val receiptCategorizationSharedVM: ReceiptCategorizationSharedVM,
) : ViewModel() {
    // # UserIntents
    fun userSubmitPartialCategorization(category: Category) {
        receiptCategorizationSharedVM.submitPartialCategorization(category)
        navUp.easyEmit(Unit)
    }

    // # Events
    val navUp = MutableSharedFlow<Unit>()

    // # State
    val partialAmountToCategorize = receiptCategorizationSharedVM.rememberedAmount.map { if (it.isZero) null else it.toString().toMoneyBigDecimal().toString() }
    val categoryButtonVMItems =
        userCategories.flow
            .map {
                it.map { category ->
                    ButtonVMItem(
                        title = category.name,
                        onClick = { userSubmitPartialCategorization(category) },
                    )
                }
            }
}
