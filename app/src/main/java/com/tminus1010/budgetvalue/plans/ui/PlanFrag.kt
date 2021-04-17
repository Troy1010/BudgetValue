package com.tminus1010.budgetvalue.plans.ui

import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import com.tminus1010.budgetvalue.*
import com.tminus1010.budgetvalue._core.extensions.toObservable
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.reflectXY
import com.tminus1010.budgetvalue._core.middleware.ui.onDone
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.ViewItemRecipeFactory3
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.itemHeaderBindingRF
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.itemTextViewBindingLRF
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.itemTextViewBindingRF
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.itemTitledDividerBindingRF
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue._core.ui.data_binding.bindText
import com.tminus1010.budgetvalue.categories.CategoriesVM
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.databinding.FragPlanBinding
import com.tminus1010.budgetvalue.databinding.ItemTextEditBinding
import com.tminus1010.budgetvalue.plans.ActivePlanVM
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class PlanFrag: Fragment(R.layout.frag_plan) {
    val vb by viewBinding(FragPlanBinding::bind)
    val activePlanVM: ActivePlanVM by activityViewModels()
    val categoriesVM: CategoriesVM by activityViewModels()
    override fun onStart() {
        super.onStart()
        // # TMTableView
        val expectedIncomeRecipeFactory = ViewItemRecipeFactory3<ItemTextEditBinding, LiveData<String>>(
            { ItemTextEditBinding.inflate(LayoutInflater.from(context)) },
            { d, vb, lifecycleOwner ->
                vb.editText.bindText(d, lifecycleOwner)
                vb.editText.onDone { activePlanVM.pushExpectedIncome(it) }
            }
        )
        val planCAsRecipeFactory = ViewItemRecipeFactory3<ItemTextEditBinding, Pair<Category, LiveData<String>?>>(
            { ItemTextEditBinding.inflate(LayoutInflater.from(context)) },
            { (category, d), vb, lifecycleOwner ->
                if (d == null) return@ViewItemRecipeFactory3
                vb.editText.bindText(d, lifecycleOwner)
                vb.editText.onDone { activePlanVM.pushActivePlanCA(category, it) }
            }
        )
        Rx.combineLatest(categoriesVM.userCategories.toObservable(viewLifecycleOwner), activePlanVM.activePlanCAs)
            .throttleLatest(150, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.computation())
            .map { (categories, planCAsItemObservableMap) ->
                val recipes2D = listOf(
                    listOf(itemHeaderBindingRF.createOne("Category"))
                            + itemTextViewBindingRF.createOne("Expected Income")
                            + itemTextViewBindingRF.createOne("Default")
                            + itemTextViewBindingRF.createMany(categories.map { it.name }),
                    listOf(itemHeaderBindingRF.createOne("Plan"))
                            + expectedIncomeRecipeFactory.createOne(activePlanVM.expectedIncome)
                            + itemTextViewBindingLRF.createOne(activePlanVM.defaultAmount)
                            + planCAsRecipeFactory.createMany(categories.map { Pair(it, planCAsItemObservableMap[it]) }))
                    .reflectXY()
                val dividerMap = categories
                    .withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to itemTitledDividerBindingRF.createOne(it.value.type.name) }
                    .mapKeys { it.key + 3 } // header row, expected income row, and default row
                Pair(recipes2D, dividerMap)
            }
            .observe(viewLifecycleOwner) { (recipes2D, dividerMap) ->
                vb.myTableViewPlan.initialize(recipes2D, true, dividerMap, 0, 1)
            }
    }
}