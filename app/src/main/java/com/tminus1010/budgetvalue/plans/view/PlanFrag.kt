package com.tminus1010.budgetvalue.plans.view

import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tminus1010.budgetvalue.*
import com.tminus1010.budgetvalue._core.all.extensions.bind
import com.tminus1010.budgetvalue._core.all.extensions.easyText
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.view.onDone
import com.tminus1010.budgetvalue._core.middleware.view.tmTableView3.*
import com.tminus1010.budgetvalue._core.middleware.view.viewBinding
import com.tminus1010.budgetvalue.categories.CategoriesVM
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.databinding.FragPlanBinding
import com.tminus1010.budgetvalue.databinding.ItemMoneyEditTextBinding
import com.tminus1010.budgetvalue.plans.presentation.ActivePlanVM
import com.tminus1010.tmcommonkotlin.core.extensions.reflectXY
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class PlanFrag : Fragment(R.layout.frag_plan) {
    private val vb by viewBinding(FragPlanBinding::bind)
    private val activePlanVM: ActivePlanVM by activityViewModels()
    private val categoriesVM: CategoriesVM by activityViewModels()
    override fun onStart() {
        super.onStart()
        // # TMTableView
        val expectedIncomeRecipeFactory = ViewItemRecipeFactory3<ItemMoneyEditTextBinding, Observable<String>>(
            { ItemMoneyEditTextBinding.inflate(LayoutInflater.from(context)) },
            { d, vb, lifecycleOwner ->
                vb.moneyedittext.bind(d, lifecycleOwner) { easyText = it }
                vb.moneyedittext.onDone { activePlanVM.userSaveExpectedIncome(it) }
            }
        )
        val planCAsRecipeFactory = ViewItemRecipeFactory3<ItemMoneyEditTextBinding, Pair<Category, Observable<String>?>>(
            { ItemMoneyEditTextBinding.inflate(LayoutInflater.from(context)) },
            { (category, d), vb, lifecycleOwner ->
                vb.moneyedittext.onDone { activePlanVM.userSaveActivePlanCA(category, it) }
                if (d == null) return@ViewItemRecipeFactory3
                vb.moneyedittext.bind(d, lifecycleOwner) { easyText = it }
            }
        )
        Rx.combineLatest(categoriesVM.userCategories, activePlanVM.activePlanCAs)
            .throttleLatest(150, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.computation())
            .map { (categories, planCAsItemObservableMap) ->
                val recipes2D = listOf(
                    listOf(recipeFactories.header.createOne("Category"))
                            + recipeFactories.textView.createOne("Expected Income")
                            + recipeFactories.textView.createOne("Default")
                            + recipeFactories.textView.createMany(categories.map { it.name }),
                    listOf(recipeFactories.header.createOne("Plan"))
                            + expectedIncomeRecipeFactory.createOne(activePlanVM.expectedIncome)
                            + recipeFactories.textViewWithLifecycle.createOne(activePlanVM.defaultAmount)
                            + planCAsRecipeFactory.createMany(categories.map { Pair(it, planCAsItemObservableMap[it]) })
                )
                    .reflectXY()
                val dividerMap = categories
                    .withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to recipeFactories.titledDivider.createOne(it.value.type.name) }
                    .mapKeys { it.key + 3 } // header row, expected income row, and default row
                Pair(recipes2D, dividerMap)
            }
            .observe(viewLifecycleOwner) { (recipes2D, dividerMap) ->
                vb.tmTableView.initialize(
                    recipeGrid = recipes2D,
                    shouldFitItemWidthsInsideTable = true,
                    dividerMap = dividerMap,
                    rowFreezeCount = 1,
                )
            }
    }
}