package com.tminus1010.buva.ui.plan

import androidx.lifecycle.ViewModel
import com.tminus1010.buva.all_layers.categoryComparator
import com.tminus1010.buva.all_layers.extensions.flatMapSourceHashMap
import com.tminus1010.buva.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.buva.app.ActivePlanInteractor
import com.tminus1010.buva.app.UserCategories
import com.tminus1010.buva.data.ActivePlanRepo
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.buva.ui.all_features.view_model_item.DividerVMItem
import com.tminus1010.buva.ui.all_features.view_model_item.MoneyEditVMItem
import com.tminus1010.buva.ui.all_features.view_model_item.TextVMItem
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
) : ViewModel() {
    // # User Intents
    fun userSaveExpectedIncome(s: String) {
        GlobalScope.launch { activePlanRepo.updateTotal(s.toMoneyBigDecimal()) }
    }

    fun userSaveActivePlanCA(category: Category, s: String) {
        GlobalScope.launch { activePlanRepo.updateCategoryAmount(category, s.toMoneyBigDecimal()) }
    }

    fun userSetActivePlanFromHistory() {
        GlobalScope.launch { activePlanInteractor.setActivePlanFromHistory() }
    }

    // # Internal
    val categoryAmounts =
        combine(activePlanRepo.activePlan.map { it.categoryAmounts }, userCategories.flow)
        { categoryAmounts, userCategories ->
            userCategories.associateWith { BigDecimal.ZERO }
                .plus(categoryAmounts)
                .toSortedMap(categoryComparator)
        }

    // # State
    val planRecipeGrid =
        categoryAmounts.flatMapSourceHashMap { it.itemFlowMap }
            .map { categoryAmountItemObservables ->
                listOf(
                    listOf(
                        TextVMItem("Category", style = TextVMItem.Style.HEADER),
                        TextVMItem("Expected Income"),
                        TextVMItem("Default"),
                        *categoryAmountItemObservables.keys.map { TextVMItem(it.name) }.toTypedArray()
                    ),
                    listOf(
                        TextVMItem("Plan", style = TextVMItem.Style.HEADER),
                        MoneyEditVMItem(text2 = activePlanRepo.activePlan.map { it.total.toString() }, onDone = { userSaveExpectedIncome(it) }),
                        TextVMItem(text3 = activePlanRepo.activePlan.map { it.defaultAmount.toString() }),
                        *categoryAmountItemObservables.map { (category, amount) -> MoneyEditVMItem(text2 = amount.map { it.toString() }, onDone = { userSaveActivePlanCA(category, it) }) }.toTypedArray()
                    ),
                ).reflectXY()
            }
    val dividerMap =
        categoryAmounts
            .map {
                it.map { it.key }.withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to it.value.type.name }
                    .mapKeys { it.key + 3 } // header row, expected row, default row
                    .mapValues { DividerVMItem(it.value) }
            }
    val buttons =
        flowOf(
            listOf(
                ButtonVMItem(
                    "Set from history",
                    onClick = { userSetActivePlanFromHistory() },
                )
            )
        )
}