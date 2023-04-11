package com.tminus1010.buva.ui.budget.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.buva.all_layers.categoryComparator
import com.tminus1010.buva.all_layers.extensions.flatMapSourceMap
import com.tminus1010.buva.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.buva.all_layers.source_objects.SourceMap
import com.tminus1010.buva.app.ActivePlanInteractor
import com.tminus1010.buva.app.UserCategories
import com.tminus1010.buva.data.ActivePlanRepo
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.ui.all_features.Navigator
import com.tminus1010.buva.ui.all_features.toDisplayStr
import com.tminus1010.buva.ui.all_features.view_model_item.*
import com.tminus1010.tmcommonkotlin.core.extensions.reflectXY
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class PlanVM @Inject constructor(
    private val activePlanRepo: ActivePlanRepo,
    private val activePlanInteractor: ActivePlanInteractor,
    private val userCategories: UserCategories,
    private val navigator: Navigator,
) : ViewModel() {
    // # User Intents
    fun userSaveExpectedIncome(s: String) {
        GlobalScope.launch { activePlanRepo.updateTotal(s.toMoneyBigDecimal()) }
    }

    fun userSaveActivePlanCA(category: Category, s: String) {
        GlobalScope.launch { activePlanRepo.updateCategoryAmount(category, s.toMoneyBigDecimal()) }
    }

    fun userSetActivePlanFromHistory() {
        GlobalScope.launch { activePlanInteractor.estimateActivePlanFromHistory() }
    }

    fun userCreateCategory() {
        navigator.navToCreateCategory()
    }

    fun userEditCategory(category: Category) {
        navigator.navToEditCategory(category)
    }

    // # Private
    private val categoryAmounts =
        combine(activePlanRepo.activePlan.map { it.categoryAmounts }, userCategories.flow)
        { categoryAmounts, userCategories ->
            userCategories.associateWith { BigDecimal.ZERO }
                .plus(categoryAmounts)
                .toSortedMap(categoryComparator)
        }

    // # State
    val tableViewVMItem =
        categoryAmounts
            .flatMapSourceMap(SourceMap(viewModelScope)) { it.itemFlowMap }
            .map { categoryAmountItemObservables ->
                TableViewVMItem(
                    recipeGrid = listOf(
                        listOf(
                            TextVMItem("Category", style = TextVMItem.Style.HEADER),
                            TextVMItem("Expected Income"),
                            TextVMItem("Default"),
                            *categoryAmountItemObservables.keys.map { TextVMItem(it.name, menuVMItems = MenuVMItems(MenuVMItem(title = "Edit", onClick = { userEditCategory(it) }), MenuVMItem(title = "Create Category", onClick = { userCreateCategory() }))) }.toTypedArray()
                        ),
                        listOf(
                            TextVMItem("Plan", style = TextVMItem.Style.HEADER),
                            MoneyEditVMItem(text2 = activePlanRepo.activePlan.map { it.total.toString() }, onDone = { userSaveExpectedIncome(it) }),
                            TextVMItem(text3 = activePlanRepo.activePlan.map { it.defaultAmount.toString() }),
                            *categoryAmountItemObservables.map { (category, amount) -> MoneyEditVMItem(text2 = amount.map { it.toString() }, onDone = { userSaveActivePlanCA(category, it) }) }.toTypedArray()
                        ),
                        listOf(
                            TextVMItem("Reset Max", style = TextVMItem.Style.HEADER),
                            TextVMItem(),
                            TextVMItem(),
                            *categoryAmountItemObservables.keys.map { TextVMItem(text1 = it.resetStrategy.toDisplayStr(), menuVMItems = MenuVMItems(MenuVMItem("Edit Category", onClick = { userEditCategory(it) }))) }.toTypedArray()
                        ),
                    ).reflectXY(),
                    dividerMap = categoryAmountItemObservables.map { it.key }.withIndex()
                        .distinctUntilChangedWith(compareBy { it.value.displayType })
                        .associate { it.index to it.value.displayType.name }
                        .mapKeys { it.key + 3 } // header row, default row
                        .mapValues { DividerVMItem(it.value) },
                    shouldFitItemWidthsInsideTable = true,
                    rowFreezeCount = 1,
                )
            }
    val buttons =
        flowOf(
            listOf(
                ButtonVMItem(
                    "Estimate from history",
                    onClick = { userSetActivePlanFromHistory() },
                )
            )
        )
}