package com.tminus1010.buva.ui.reconciliation

import androidx.lifecycle.ViewModel
import com.tminus1010.buva.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.buva.app.ActiveReconciliationInteractor
import com.tminus1010.buva.app.ActiveReconciliationInteractor2
import com.tminus1010.buva.app.BudgetedForActiveReconciliationInteractor
import com.tminus1010.buva.app.UserCategories
import com.tminus1010.buva.data.ActiveReconciliationRepo
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.ui.all_features.view_model_item.*
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class AccountsReconciliationVM @Inject constructor(
    budgetedForActiveReconciliationInteractor: BudgetedForActiveReconciliationInteractor,
    activeReconciliationInteractor: ActiveReconciliationInteractor,
    private val activeReconciliationInteractor2: ActiveReconciliationInteractor2,
    private val activeReconciliationRepo: ActiveReconciliationRepo,
    userCategories: UserCategories,
) : ViewModel() {
    // # User Intents
    fun userSetCategoryAmount(category: Category, s: String) {
        suspend { activeReconciliationRepo.pushCategoryAmount(category, s.toMoneyBigDecimal()) }
            .observe(GlobalScope)
    }

    fun userDumpIntoCategory(category: Category) {
        suspend { activeReconciliationInteractor2.fillIntoCategory(category) }
            .observe(GlobalScope)
    }

    // # State
    val reconciliationTableView =
        combine(userCategories.flow, activeReconciliationInteractor.categoryAmountsAndTotal, budgetedForActiveReconciliationInteractor.categoryAmountsAndTotal)
        { categories, activeReconciliation, budgetedWithActiveReconciliation ->
            TableViewVMItem(
                recipeGrid = listOf(
                    listOf(
                        HeaderPresentationModel("Categories"),
                        HeaderPresentationModel("Reconcile"),
                        BudgetHeaderPresentationModel("Budgeted", budgetedWithActiveReconciliation.total.toString()),
                    ),
                    listOf(
                        TextVMItem("Total"),
                        TextVMItem(activeReconciliation.total.toString()),
                        AmountPresentationModel(budgetedWithActiveReconciliation.total),
                    ),
                    listOf(
                        TextVMItem("Default"),
                        TextVMItem(activeReconciliation.defaultAmount.toString()),
                        AmountPresentationModel(budgetedWithActiveReconciliation.defaultAmount) { budgetedWithActiveReconciliation.isDefaultAmountValid },
                    ),
                    *categories.map { category ->
                        listOf(
                            TextVMItem(category.name),
                            CategoryAmountPresentationModel(category, activeReconciliation.categoryAmounts[category], ::userSetCategoryAmount, menuVMItems = MenuVMItems(MenuVMItem("Dump into category", onClick = { userDumpIntoCategory(category) }))),
                            AmountPresentationModel(budgetedWithActiveReconciliation.categoryAmounts[category]) { budgetedWithActiveReconciliation.isValid(category) },
                        )
                    }.toTypedArray(),
                ),
                dividerMap = categories.withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to it.value.type.name }
                    .mapKeys { it.key + 3 } // header row, default row
                    .mapValues { DividerVMItem(it.value) },
                shouldFitItemWidthsInsideTable = true,
                rowFreezeCount = 1,
            )
        }
}