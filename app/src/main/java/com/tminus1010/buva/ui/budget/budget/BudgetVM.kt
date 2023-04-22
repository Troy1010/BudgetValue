package com.tminus1010.buva.ui.budget.budget

import androidx.lifecycle.ViewModel
import com.tminus1010.buva.app.BudgetedInteractor
import com.tminus1010.buva.app.UserCategories
import com.tminus1010.buva.domain.ReconciliationStrategyGroup
import com.tminus1010.buva.domain.ResetStrategy
import com.tminus1010.buva.ui.all_features.view_model_item.AmountPresentationModel
import com.tminus1010.buva.ui.all_features.view_model_item.DividerVMItem
import com.tminus1010.buva.ui.all_features.view_model_item.TableViewVMItem
import com.tminus1010.buva.ui.all_features.view_model_item.TextVMItem
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class BudgetVM @Inject constructor(
    private val userCategories: UserCategories,
    private val budgetedInteractor: BudgetedInteractor,
) : ViewModel() {
    // # State
    val tableViewVMItem =
        combine(userCategories.flow, budgetedInteractor.budgeted)
        { categories, budgeted ->
            TableViewVMItem(
                recipeGrid = listOf(
                    listOf(
                        TextVMItem("Total"),
                        AmountPresentationModel(budgeted.total),
                    ),
                    listOf(
                        TextVMItem("Default"),
                        AmountPresentationModel(budgeted.defaultAmount, validation = { budgeted.defaultValidationResult }),
                    ),
                    *categories.map { category ->
                        listOf(
                            TextVMItem(category.name),
                            TextVMItem(
                                text1 = when (category.reconciliationStrategyGroup) {
                                    is ReconciliationStrategyGroup.Reservoir ->
                                        "${budgeted.categoryAmounts[category]?.toString()} (${(category.reconciliationStrategyGroup.resetStrategy as? ResetStrategy.Basic)?.budgetedMax})"
                                    else ->
                                        budgeted.categoryAmounts[category]?.toString()
                                },
                                validation = { budgeted.validation(category) },
                            ),
                        )
                    }.toTypedArray(),
                ),
                dividerMap = categories.withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.displayType })
                    .associate { it.index to it.value.displayType.name }
                    .mapKeys { it.key + 2 } // total row, default row
                    .mapValues { DividerVMItem(it.value) },
                shouldFitItemWidthsInsideTable = true,
            )
        }
}