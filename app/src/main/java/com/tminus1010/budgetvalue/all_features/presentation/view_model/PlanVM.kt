package com.tminus1010.budgetvalue.all_features.presentation.view_model

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.flatMapSourceHashMap
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue.all_features.app.model.Category
import com.tminus1010.budgetvalue.all_features.presentation.model.ButtonVMItem
import com.tminus1010.budgetvalue.all_features.presentation.model.DividerVMItem
import com.tminus1010.budgetvalue.all_features.presentation.model.MoneyEditVMItem
import com.tminus1010.budgetvalue.all_features.presentation.model.TextVMItem
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.plans.app.interactor.SetActivePlanFromHistoryInteractor
import com.tminus1010.budgetvalue.plans.data.ActivePlanRepo
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
    private val setActivePlanFromHistoryInteractor: SetActivePlanFromHistoryInteractor,
    private val categoriesInteractor: CategoriesInteractor,
) : ViewModel() {
    // # User Intents
    fun userSaveExpectedIncome(s: String) {
        GlobalScope.launch { activePlanRepo.updateTotal(s.toMoneyBigDecimal()) }
    }

    fun userSaveActivePlanCA(category: Category, s: String) {
        GlobalScope.launch { activePlanRepo.updateCategoryAmount(category, s.toMoneyBigDecimal()) }
    }

    fun userSetActivePlanFromHistory() {
        GlobalScope.launch { setActivePlanFromHistoryInteractor.setActivePlanFromHistory() }
    }

    // # Internal
    val categoryAmounts =
        combine(
            activePlanRepo.activePlan.map { it.categoryAmounts },
            categoriesInteractor.userCategories,
        )
        { categoryAmounts, userCategories ->
            userCategories.associateWith { BigDecimal.ZERO }
                .plus(categoryAmounts)
        }

    // # State
    val recipeGrid =
        categoryAmounts.flatMapSourceHashMap { it.itemFlowMap }
            .map { categoryAmountItemObservables ->
                listOf(
                    listOf(
                        TextVMItem("Category", style = TextVMItem.Style.HEADER),
                        TextVMItem("Expected Income"),
                        TextVMItem("Default"),
                        *categoryAmountItemObservables.keys.map { TextVMItem(it.name) }.logx("categoryTextVMItems").toTypedArray()
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