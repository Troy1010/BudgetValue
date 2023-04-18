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
import com.tminus1010.buva.data.CategoryRepo
import com.tminus1010.buva.domain.*
import com.tminus1010.buva.ui.all_features.Navigator
import com.tminus1010.buva.ui.all_features.ThrobberSharedVM
import com.tminus1010.buva.ui.all_features.view_model_item.*
import com.tminus1010.tmcommonkotlin.core.extensions.reflectXY
import com.tminus1010.tmcommonkotlin.coroutines.extensions.use
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
    private val categoryRepo: CategoryRepo,
    private val throbberSharedVM: ThrobberSharedVM,
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

    fun userSwitchToReservoir(category: Category) {
        GlobalScope.launch { categoryRepo.push(category.copy(reconciliationStrategyGroup = ReconciliationStrategyGroup.Reservoir())) }.use(throbberSharedVM)
    }

    fun userSwitchToAlways(category: Category) {
        GlobalScope.launch { categoryRepo.push(category.copy(reconciliationStrategyGroup = ReconciliationStrategyGroup.Always)) }.use(throbberSharedVM)
    }

    fun userFillIntoCategory(category: Category) {
        GlobalScope.launch { activePlanInteractor.fillIntoCategory(category) }
    }

    private fun userSetBudgetMax(category: Category, s: String) {
        GlobalScope.launch { categoryRepo.push(category.copy(reconciliationStrategyGroup = ReconciliationStrategyGroup.Reservoir(ResetStrategy.Basic(s.toMoneyBigDecimal())))) }.use(throbberSharedVM)
    }

    private fun userSetTimeToAchieve(category: Category, s: String) {
        val max = (category.reconciliationStrategyGroup.resetStrategy as ResetStrategy.Basic).budgetedMax!!
        val timeToAchieve = s.ifEmpty { null }?.toBigDecimal()
        GlobalScope.launch { activePlanRepo.updateCategoryAmount(category, MiscUtil.calcPlanValue(timeToAchieve, max) ?: BigDecimal.ZERO) }
    }

    // # Private
    private val categoryAmounts =
        combine(activePlanRepo.activePlan.map { it.categoryAmounts }, userCategories.flow)
        { categoryAmounts, userCategories ->
            userCategories.associateWith { BigDecimal.ZERO }
                .plus(categoryAmounts)
        }

    // # State
    val tableViewVMItem =
        categoryAmounts
            .flatMapSourceMap(SourceMap(viewModelScope)) { it.itemFlowMap }
            .map { categoryAmountItemObservables ->
                val categoryAmountItemObservablesRedefined = categoryAmountItemObservables.toSortedMap(categoryComparator)
                val getSharedMenuItems = { category: Category ->
                    MenuVMItems(
                        MenuVMItem(
                            title = "Edit Category",
                            onClick = { userEditCategory(category) },
                        ),
                        MenuVMItem(
                            title = "Create Category",
                            onClick = { userCreateCategory() }
                        ),
                        if (category.reconciliationStrategyGroup is ReconciliationStrategyGroup.Always)
                            MenuVMItem(
                                title = "Switch to Reservoir",
                                onClick = { userSwitchToReservoir(category) },
                            )
                        else
                            MenuVMItem(
                                title = "Switch to Always",
                                onClick = { userSwitchToAlways(category) },
                            ),
                    )
                }
                TableViewVMItem(
                    recipeGrid = listOf(
                        listOf(
                            TextVMItem(text1 = "Category", style = TextVMItem.Style.HEADER),
                            TextVMItem(text1 = "Expected Income"),
                            TextVMItem(text1 = "Default"),
                            *categoryAmountItemObservablesRedefined.keys.map { category ->
                                TextVMItem(
                                    text1 = category.name,
                                    menuVMItems = getSharedMenuItems(category)
                                )
                            }.toTypedArray()
                        ),
                        listOf(
                            TextVMItem(text1 = "Plan", style = TextVMItem.Style.HEADER),
                            MoneyEditVMItem(text2 = activePlanRepo.activePlan.map { it.total.toString() }, onDone = { userSaveExpectedIncome(it) }),
                            TextVMItem(text3 = activePlanRepo.activePlan.map { it.defaultAmount.toString() }),
                            *categoryAmountItemObservablesRedefined.map { (category, amount) ->
                                MoneyEditVMItem(
                                    text2 = amount.map { it.toString() },
                                    onDone = { userSaveActivePlanCA(category, it) },
                                    menuVMItems = MenuVMItems(MenuVMItem("Fill into category", onClick = { userFillIntoCategory(category) }))
                                )
                            }.toTypedArray()
                        ),
                        listOf(
                            TextVMItem(text1 = "Max", style = TextVMItem.Style.HEADER),
                            TextVMItem(),
                            TextVMItem(),
                            *categoryAmountItemObservablesRedefined.keys.map { category ->
                                when (category.reconciliationStrategyGroup) {
                                    is ReconciliationStrategyGroup.Unlimited,
                                    is ReconciliationStrategyGroup.Always,
                                    ->
                                        TextVMItem(
                                            text1 = "n/a",
                                            menuVMItems = getSharedMenuItems(category),
                                        )
                                    is ReconciliationStrategyGroup.Reservoir ->
                                        MoneyEditVMItem(
                                            text1 = when (val x = category.reconciliationStrategyGroup.resetStrategy) {
                                                is ResetStrategy.Basic -> x.budgetedMax.toString()
                                            },
                                            onDone = { userSetBudgetMax(category, it) },
                                            validation = { Validate.resetMax(it?.toMoneyBigDecimal()) },
                                        )
                                }
                            }.toTypedArray(),
                        ),
                        listOf(
                            TextVMItem(text1 = "Time to achieve", style = TextVMItem.Style.HEADER),
                            TextVMItem(),
                            TextVMItem(),
                            *categoryAmountItemObservablesRedefined.keys.map { category ->
                                runCatching {
                                    MoneyEditVMItem(
                                        text1 = MiscUtil.calcTimeToAchieve(
                                            planValue = categoryAmountItemObservablesRedefined[category]!!.value,
                                            resetMax = (category.reconciliationStrategyGroup.resetStrategy as ResetStrategy.Basic).budgetedMax!!
                                        )?.toString(),
                                        onDone = { userSetTimeToAchieve(category, it) },
                                    )
                                }.getOrElse {
                                    TextVMItem(
                                        text1 = "n/a",
                                        menuVMItems = getSharedMenuItems(category),
                                    )
                                }
                            }.toTypedArray(),
                        ),
                    ).reflectXY(),
                    dividerMap = categoryAmountItemObservablesRedefined.map { it.key }.withIndex()
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