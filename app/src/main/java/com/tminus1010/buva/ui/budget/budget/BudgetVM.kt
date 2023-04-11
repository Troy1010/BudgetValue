package com.tminus1010.buva.ui.budget.budget

import androidx.lifecycle.ViewModel
import com.tminus1010.buva.app.HistoryInteractor
import com.tminus1010.buva.app.UserCategories
import com.tminus1010.buva.ui.all_features.view_model_item.*
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class BudgetVM @Inject constructor(
    private val userCategories: UserCategories,
    private val historyInteractor: HistoryInteractor,
) : ViewModel() {
    // # State
    val tableViewVMItem =
        combine(userCategories.flow, historyInteractor.entireHistory)
        { categories, categoryAmountsAndTotalsAggregate ->
            TableViewVMItem(
                recipeGrid = listOf(
                    listOf(
                        TextVMItem("Total"),
                        AmountPresentationModel(categoryAmountsAndTotalsAggregate.addedTogether.total),
                    ),
                    listOf(
                        TextVMItem("Default"),
                        AmountPresentationModel(categoryAmountsAndTotalsAggregate.addedTogether.defaultAmount),
                    ),
                    *categories.map { category ->
                        listOf(
                            TextVMItem(category.name),
                            AmountPresentationModel(categoryAmountsAndTotalsAggregate.addedTogether.categoryAmounts[category]),
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
    val buttons =
        flowOf(
            listOf<ButtonVMItem>(
            )
        )
}