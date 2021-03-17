package com.tminus1010.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.jakewharton.rxbinding4.view.clicks
import com.tminus1010.budgetvalue.*
import com.tminus1010.budgetvalue.dependency_injection.ViewModelProviders
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.appComponent
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.domain
import com.tminus1010.budgetvalue.layer_ui.TMTableView.ViewItemRecipeFactory
import com.tminus1010.budgetvalue.layer_ui.TMTableView2.RecipeGrid
import com.tminus1010.budgetvalue.layer_ui.misc.bindIncoming
import com.tminus1010.budgetvalue.layer_ui.misc.bindOutgoing
import com.tminus1010.budgetvalue.model_domain.Category
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.frag_reconcile.*
import kotlinx.android.synthetic.main.tableview_header_income.view.*
import java.math.BigDecimal

class ReconcileFrag : Fragment(R.layout.frag_reconcile), IViewModels {
    override val viewModelProviders by lazy { ViewModelProviders(requireActivity(), appComponent) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Clicks
        btn_save.clicks().subscribe {
            activeReconciliationVM2.intentSaveReconciliation.onNext(Unit)
            activePlanVM.intentSaveActivePlan.onNext(Unit)
        }
        // # TMTableView
        val cellRecipeFactory = ViewItemRecipeFactory.createCellRecipeFactory(requireContext())
        val headerRecipeFactory = ViewItemRecipeFactory.createHeaderRecipeFactory(requireContext())
        val headerRecipeFactory_numbered = ViewItemRecipeFactory<LinearLayout, Pair<String, Observable<BigDecimal>>>(
            { View.inflate(requireContext(), R.layout.tableview_header_income, null) as LinearLayout },
            { v, d ->
                v.textview_header.text = d.first
                v.textview_number.bindIncoming(d.second)
            })
        val reconcileCARecipeFactory = ViewItemRecipeFactory<EditText, Pair<Category, Observable<BigDecimal>>>(
            { View.inflate(context, R.layout.tableview_text_edit, null) as EditText },
            { v, (category, d) ->
                v.bindIncoming(d)
                v.bindOutgoing(activeReconciliationVM.intentPushActiveReconcileCA, { s -> category to s.toMoneyBigDecimal() }) { it.second }
            }
        )
        val oneWayRecipeFactory = ViewItemRecipeFactory<TextView, Observable<BigDecimal>>(
            { View.inflate(context, R.layout.tableview_text_view, null) as TextView },
            { v, d -> v.bindIncoming(d) }
        )
        val titledDividerRecipeFactory = ViewItemRecipeFactory<TextView, String>(
            { View.inflate(context, R.layout.tableview_titled_divider, null) as TextView },
            { v, s -> v.text = s }
        )
        combineLatestAsTuple(activeReconciliationVM.rowDatas, domain.userCategories, myTableView_1.widthObservable, budgetedVM.categoryAmounts.value.itemObservableMap2)
            .observeOn(AndroidSchedulers.mainThread())
            .observe(viewLifecycleOwner) { (rowDatas, activeCategories, width, budgetedCA) ->
                val dividerMap = activeCategories
                    .withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to titledDividerRecipeFactory.createOne(it.value.type.name) }
                    .mapKeys { it.key + 2 } // header row, default row
                myTableView_1.initialize(
                    recipeGrid = RecipeGrid(listOf(
                        headerRecipeFactory.createOne2("Category")
                                + cellRecipeFactory.createOne2("Default")
                                + cellRecipeFactory.createMany(rowDatas.map { it.category.name }),
                        headerRecipeFactory_numbered.createOne2(Pair("Plan", activePlanVM.expectedIncome))
                                + oneWayRecipeFactory.createOne2(activePlanVM.defaultAmount)
                                + oneWayRecipeFactory.createMany(rowDatas.map { it.plan }),
                        headerRecipeFactory.createOne2("Actual")
                                + cellRecipeFactory.createOne2("")
                                + oneWayRecipeFactory.createMany(rowDatas.map { it.actual }),
                        headerRecipeFactory.createOne2("Reconcile")
                                + oneWayRecipeFactory.createOne2(activeReconciliationVM2.defaultAmount)
                                + reconcileCARecipeFactory.createMany(rowDatas.map { it.category to it.reconcile }),
                        headerRecipeFactory_numbered.createOne2(Pair("Budgeted", accountsVM.accountsTotal))
                                + oneWayRecipeFactory.createOne2(budgetedVM.defaultAmount)
                                + oneWayRecipeFactory.createMany(rowDatas.map { budgetedCA[it.category]!! })
                    ).reflectXY(), fixedWidth = width),
                    dividerMap = dividerMap,
                    colFreezeCount = 0,
                    rowFreezeCount = 1
                )
            }
    }
}
