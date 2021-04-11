package com.tminus1010.budgetvalue.plans

import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import com.tminus1010.budgetvalue.*
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.reflectXY
import com.tminus1010.budgetvalue._core.middleware.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.middleware.ui.bindIncoming
import com.tminus1010.budgetvalue._core.middleware.ui.bindOutgoing
import com.tminus1010.budgetvalue._core.middleware.ui.onDone
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView.ViewItemRecipeFactory
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue.categories.CategoriesVM
import com.tminus1010.budgetvalue.databinding.FragPlanBinding
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class PlanFrag: Fragment(R.layout.frag_plan) {
    val vb by viewBinding(FragPlanBinding::bind)
    val activePlanVM: ActivePlanVM by activityViewModels()
    val categoriesVM: CategoriesVM by activityViewModels()
    override fun onStart() {
        super.onStart()
        // # TMTableView
        val cellRecipeFactory = ViewItemRecipeFactory.createCellRecipeFactory(requireContext())
        val headerRecipeFactory = ViewItemRecipeFactory.createHeaderRecipeFactory(requireContext())
        val expectedIncomeRecipeFactory = ViewItemRecipeFactory<EditText, LiveData<String>>(
            { View.inflate(context, R.layout.item_text_edit, null) as EditText },
            { v, d ->
                v.bindIncoming(viewLifecycleOwner, d)
                v.onDone { activePlanVM.pushExpectedIncome(it) }
            }
        )
        val planCAsRecipeFactory = ViewItemRecipeFactory<EditText, Pair<Category, LiveData<String>?>>(
            { View.inflate(context, R.layout.item_text_edit, null) as EditText },
            { v, (category, d) ->
                if (d == null) return@ViewItemRecipeFactory
                v.bindIncoming(viewLifecycleOwner, d)
                v.onDone { activePlanVM.pushActivePlanCA(category, it) }
            }
        )
        val oneWayRecipeFactory2 = ViewItemRecipeFactory<TextView, LiveData<String>>(
            { View.inflate(context, R.layout.item_text_view, null) as TextView },
            { v, d -> v.bindIncoming(viewLifecycleOwner, d) }
        )
        val titledDividerRecipeFactory = ViewItemRecipeFactory<TextView, String>(
            { View.inflate(context, R.layout.item_titled_divider, null) as TextView },
            { v, s -> v.text = s }
        )
        Rx.combineLatest(categoriesVM.userCategories, activePlanVM.activePlanCAs)
            .debounce(150, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.computation())
            .map { (categories, planCAsItemObservableMap) ->
                val recipes2D = listOf(
                    headerRecipeFactory.createOne2("Category")
                            + cellRecipeFactory.createOne2("Expected Income")
                            + cellRecipeFactory.createOne2("Default")
                            + cellRecipeFactory.createMany(categories.map { it.name }),
                    headerRecipeFactory.createOne2("Plan")
                            + expectedIncomeRecipeFactory.createOne2(activePlanVM.expectedIncome)
                            + oneWayRecipeFactory2.createOne2(activePlanVM.defaultAmount)
                            + planCAsRecipeFactory.createMany(categories.map { Pair(it, planCAsItemObservableMap[it]) }))
                    .reflectXY()
                val dividerMap = categories
                    .withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to titledDividerRecipeFactory.createOne(it.value.type.name) }
                    .mapKeys { it.key + 3 } // header row, expected income row, and default row
                Pair(recipes2D, dividerMap)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .observe(viewLifecycleOwner) { (recipes2D, dividerMap) ->
                vb.myTableViewPlan.initialize(recipes2D, true, dividerMap, 0, 1)
            }
    }
}