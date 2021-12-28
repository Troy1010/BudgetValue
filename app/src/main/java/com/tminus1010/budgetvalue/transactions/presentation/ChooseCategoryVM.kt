package com.tminus1010.budgetvalue.transactions.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.all.extensions.easyEmit
import com.tminus1010.budgetvalue._core.all.extensions.isZero
import com.tminus1010.budgetvalue._core.all.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.presentation.model.ButtonVMItem
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.transactions.app.ReceiptCategorizationInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx3.asFlow
import javax.inject.Inject

@HiltViewModel
class ChooseCategoryVM @Inject constructor(
    categoriesInteractor: CategoriesInteractor,
    private val receiptCategorizationInteractor: ReceiptCategorizationInteractor,
) : ViewModel() {
    // # Presentation State
    val partialAmountToCategorize = receiptCategorizationInteractor.currentChosenAmount.map { if (it.isZero) null else it.toString().toMoneyBigDecimal().toString() }
    val categoryButtonVMItems =
        categoriesInteractor.userCategories.asFlow()
            .map {
                it.map { category ->
                    ButtonVMItem(
                        title = category.name,
                        onClick = {
                            receiptCategorizationInteractor.currentCategory.easyEmit(category)
                            receiptCategorizationInteractor.submitPartialCategorization()
                            navUp.easyEmit(Unit)
                        },
                    )
                }
            }

    // # Presentation Events
    val navUp = MutableSharedFlow<Unit>()
}
