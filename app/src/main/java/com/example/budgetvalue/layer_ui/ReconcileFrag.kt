package com.example.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.budgetvalue.App
import com.example.budgetvalue.R
import com.example.budgetvalue.layer_ui.TMTableView.*
import com.example.budgetvalue.layer_ui.TMTableView.CellRecipeFactory.DefaultType
import com.example.budgetvalue.layer_ui.misc.rxBind
import com.example.budgetvalue.layer_ui.misc.rxBindOneWay
import com.example.budgetvalue.reflectXY
import com.example.budgetvalue.toBigDecimal2
import com.tminus1010.tmcommonkotlin.misc.createVmFactory
import com.tminus1010.tmcommonkotlin_rx.observe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.frag_reconcile.*
import kotlinx.android.synthetic.main.tableview_header_income.view.*
import java.math.BigDecimal

class ReconcileFrag : Fragment(R.layout.frag_reconcile) {
    val app by lazy { requireActivity().application as App }
    val categoriesAppVM by lazy { app.appComponent.getCategoriesAppVM() }
    val transactionsVM: TransactionsVM by activityViewModels { createVmFactory { TransactionsVM(app.appComponent.getRepo()) } }
    val accountsVM: AccountsVM by activityViewModels{ createVmFactory { AccountsVM(app.appComponent.getRepo()) }}
    val planVM: PlanVM by activityViewModels{ createVmFactory { PlanVM(app.appComponent.getRepo(), categoriesAppVM) }}
    val reconcileVM: ReconcileVM by activityViewModels { createVmFactory { ReconcileVM(app.appComponent.getRepo(), transactionsVM.spends, accountsVM.accountsTotal, planVM) } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTableDataObserver()
    }

    fun setupTableDataObserver() {
        val cellRecipeBuilder = CellRecipeFactory(requireContext(), DefaultType.CELL)
        val headerRecipeBuilder = CellRecipeFactory(requireContext(), DefaultType.HEADER)
        val headerRecipeBuilder_numbered = CellRecipeFactory<LinearLayout, Pair<String, Observable<BigDecimal>>>(
            { View.inflate(requireContext(), R.layout.tableview_header_income, null) as LinearLayout },
            {v, d ->
                v.textview_header.text = d.first
                v.textview_number.rxBindOneWay(d.second)
            })
        val inputRecipeBuilder = CellRecipeFactory<EditText, BehaviorSubject<BigDecimal>>(
            { View.inflate(context, R.layout.tableview_text_edit, null) as EditText },
            { v, bs -> v.rxBind(bs, { it.toBigDecimal2() } )}
        )
        val oneWayCellRecipeBuilder = CellRecipeFactory<TextView, Observable<BigDecimal>>(
            { View.inflate(context, R.layout.tableview_text_view, null) as TextView },
            { v, observable -> v.rxBindOneWay(observable)}
        )
        reconcileVM.rowDatas
            .observeOn(AndroidSchedulers.mainThread())
            .observe(viewLifecycleOwner) { rowDatas ->
                myTableView_1.setRecipes(
                    listOf(
                        headerRecipeBuilder.createOne("Category")
                                + cellRecipeBuilder.createOne("Default")
                                + cellRecipeBuilder.createMany(rowDatas.map { it.category.name }),
                        headerRecipeBuilder_numbered.createOne(Pair("Plan", planVM.expectedIncome))
                                + oneWayCellRecipeBuilder.createOne(planVM.difference)
                                + oneWayCellRecipeBuilder.createMany(rowDatas.map { it.plan }),
                        headerRecipeBuilder.createOne("Actual")
                                + cellRecipeBuilder.createOne("")
                                + oneWayCellRecipeBuilder.createMany(rowDatas.map { it.actual }),
                        headerRecipeBuilder.createOne("Reconcile")
                                + oneWayCellRecipeBuilder.createOne(reconcileVM.reconcileDefault)
                                + inputRecipeBuilder.createMany(rowDatas.map { it.reconcile }),
                        headerRecipeBuilder_numbered.createOne(Pair("Budgeted",accountsVM.accountsTotal))
                                + oneWayCellRecipeBuilder.createOne(reconcileVM.uncategorizedBudgeted)
                                + oneWayCellRecipeBuilder.createMany(rowDatas.map { it.budgeted })
                    ).reflectXY()
                )
            }
    }
}
