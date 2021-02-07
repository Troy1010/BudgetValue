package com.tminus1010.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.*
import com.tminus1010.budgetvalue.extensions.activityViewModels2
import com.tminus1010.budgetvalue.extensions.distinctUntilChangedWith
import com.tminus1010.budgetvalue.layer_ui.TMTableView.ViewItemRecipeFactory
import com.tminus1010.budgetvalue.layer_ui.TMTableView2.RecipeGrid
import com.tminus1010.budgetvalue.layer_ui.misc.bindIncoming
import com.tminus1010.budgetvalue.layer_ui.misc.bindOutgoing
import com.tminus1010.budgetvalue.model_app.Category
import com.tminus1010.tmcommonkotlin_rx.observe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.frag_plan.*
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class PlanFrag: Fragment(R.layout.frag_plan) {
    val app by lazy { requireActivity().application as App }
    val repo by lazy { app.appComponent.getRepo() }
    val categoriesAppVM by lazy { app.appComponent.getCategoriesAppVM() }
    val activePlanVM : ActivePlanVM by activityViewModels2 { ActivePlanVM(repo, categoriesAppVM) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBinds()
    }

    private fun setupBinds() {
        val cellRecipeFactory = ViewItemRecipeFactory.createCellRecipeFactory(requireContext())
        val headerRecipeFactory = ViewItemRecipeFactory.createHeaderRecipeFactory(requireContext())
        val expectedIncomeRecipeFactory = ViewItemRecipeFactory<EditText, Observable<BigDecimal>>(
            { View.inflate(context, R.layout.tableview_text_edit, null) as EditText },
            { view, bs ->
                view.bindIncoming(bs)
                view.bindOutgoing(activePlanVM.intentPushExpectedIncome, { it.toBigDecimalSafe() }) { it }
            }
        )
        val planCAsRecipeFactory = ViewItemRecipeFactory<EditText, Pair<Category, Observable<BigDecimal>>>(
            { View.inflate(context, R.layout.tableview_text_edit, null) as EditText },
            { view, (category, bs) ->
                view.bindIncoming(bs)
                view.bindOutgoing(activePlanVM.intentPushPlanCA, { Pair(category, it.toBigDecimalSafe()) }) { it.second }
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
        combineLatestAsTuple(activePlanVM.planCAs.value.itemObservableMap2, activePlanVM.activeCategories, myTableView_plan.widthObservable)
            .debounce(100, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.computation())
            .map { (planCAsItemObservableMap, activeCategories, width) ->
                val recipes2D = RecipeGrid(listOf(
                    headerRecipeFactory.createOne2("Category")
                            + cellRecipeFactory.createOne2("Expected Income")
                            + cellRecipeFactory.createOne2("Default")
                            + cellRecipeFactory.createMany(activeCategories.map { it.name }),
                    headerRecipeFactory.createOne2("Plan")
                            + expectedIncomeRecipeFactory.createOne2(activePlanVM.expectedIncome)
                            + oneWayRecipeBuilder.createOne2(activePlanVM.defaultAmount)
                            + planCAsRecipeFactory.createMany(activeCategories.map { Pair(it, planCAsItemObservableMap[it]!!) }))
                    .reflectXY(), fixedWidth = width)
                val dividerMap = activeCategories
                    .withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to titledDividerRecipeFactory.createOne(it.value.type.name) }
                    .mapKeys { it.key + 3 } // header row, expected income row, and default row
                Pair(recipes2D, dividerMap)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .observe(this) { (recipes2D, dividerMap) ->
                myTableView_plan?.initialize(recipes2D, dividerMap, 0, 1)
            }
    }
}