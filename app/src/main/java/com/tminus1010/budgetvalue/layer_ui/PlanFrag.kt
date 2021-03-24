package com.tminus1010.budgetvalue.layer_ui

import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.*
import com.tminus1010.budgetvalue.databinding.FragPlanBinding
import com.tminus1010.budgetvalue.dependency_injection.ViewModelProviders
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.appComponent
import com.tminus1010.budgetvalue.features.categories.Category
import com.tminus1010.budgetvalue.middleware.Rx
import com.tminus1010.budgetvalue.middleware.reflectXY
import com.tminus1010.budgetvalue.middleware.toMoneyBigDecimal
import com.tminus1010.budgetvalue.middleware.ui.bindIncoming
import com.tminus1010.budgetvalue.middleware.ui.bindOutgoing
import com.tminus1010.budgetvalue.middleware.ui.tmTableView.ViewItemRecipeFactory
import com.tminus1010.budgetvalue.middleware.ui.viewBinding
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class PlanFrag: Fragment(R.layout.frag_plan), IViewModels {
    val vb by viewBinding(FragPlanBinding::bind)
    override val viewModelProviders by lazy { ViewModelProviders(requireActivity(), appComponent) }
    override fun onStart() {
        super.onStart()
        // # TMTableView
        val cellRecipeFactory = ViewItemRecipeFactory.createCellRecipeFactory(requireContext())
        val headerRecipeFactory = ViewItemRecipeFactory.createHeaderRecipeFactory(requireContext())
        val expectedIncomeRecipeFactory = ViewItemRecipeFactory<EditText, Observable<BigDecimal>>(
            { View.inflate(context, R.layout.tableview_text_edit, null) as EditText },
            { view, bs ->
                view.bindIncoming(bs)
                view.bindOutgoing(activePlanVM.intentPushExpectedIncome, { it.toMoneyBigDecimal() }) { it }
            }
        )
        val planCAsRecipeFactory = ViewItemRecipeFactory<EditText, Pair<Category, Observable<BigDecimal>?>>(
            { View.inflate(context, R.layout.tableview_text_edit, null) as EditText },
            { view, (category, d) ->
                if (d == null) return@ViewItemRecipeFactory
                view.bindIncoming(d)
                view.bindOutgoing(activePlanVM.intentPushActivePlanCA, { Pair(category, it.toMoneyBigDecimal()) }) { it.second }
            }
        )
        val oneWayRecipeBuilder = ViewItemRecipeFactory<TextView, Observable<BigDecimal>>(
            { View.inflate(context, R.layout.tableview_text_view, null) as TextView },
            { v, bs -> v.bindIncoming(bs) }
        )
        val titledDividerRecipeFactory = ViewItemRecipeFactory<TextView, String>(
            { View.inflate(context, R.layout.tableview_titled_divider, null) as TextView },
            { v, s -> v.text = s }
        )
        Rx.combineLatest(categoriesVM.userCategories, activePlanVM.activePlanCAs.value.itemObservableMap2)
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
                            + oneWayRecipeBuilder.createOne2(activePlanVM.defaultAmount)
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