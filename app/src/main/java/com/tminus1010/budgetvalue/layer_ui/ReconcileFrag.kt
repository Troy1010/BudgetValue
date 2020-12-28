package com.tminus1010.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.App
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.extensions.activityViewModels2
import com.tminus1010.budgetvalue.layer_ui.TMTableView.*
import com.tminus1010.budgetvalue.layer_ui.misc.bind
import com.tminus1010.budgetvalue.layer_ui.misc.bindIncoming
import com.tminus1010.budgetvalue.reflectXY
import com.tminus1010.budgetvalue.toBigDecimalSafe
import com.tminus1010.tmcommonkotlin_rx.observe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.frag_reconcile.*
import kotlinx.android.synthetic.main.tableview_header_income.view.*
import java.math.BigDecimal

class ReconcileFrag : Fragment(R.layout.frag_reconcile) {
    val app by lazy { requireActivity().application as App }
    val repo by lazy { app.appComponent.getRepo() }
    val transactionsVM: TransactionsVM by activityViewModels2 { TransactionsVM(repo) }
    val accountsVM: AccountsVM by activityViewModels2 { AccountsVM(repo) }
    val categoriesAppVM by lazy { app.appComponent.getCategoriesAppVM() }
    val planVM: PlanVM by activityViewModels2 { PlanVM(repo, categoriesAppVM) }
    val reconcileVM: ReconcileVM by activityViewModels2 { ReconcileVM(repo, transactionsVM.spends, accountsVM.accountsTotal, planVM) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBinds()
    }

    fun setupBinds() {
        val cellRecipeFactory = ViewItemRecipeFactory.createCellRecipeFactory(requireContext())
        val headerRecipeFactory = ViewItemRecipeFactory.createHeaderRecipeFactory(requireContext())
        val headerRecipeFactory_numbered = ViewItemRecipeFactory<LinearLayout, Pair<String, Observable<BigDecimal>>>(
            { View.inflate(requireContext(), R.layout.tableview_header_income, null) as LinearLayout },
            {v, d ->
                v.textview_header.text = d.first
                v.textview_number.bindIncoming(d.second)
            })
        val twoWayRecipeFactory = ViewItemRecipeFactory<EditText, BehaviorSubject<BigDecimal>>(
            { View.inflate(context, R.layout.tableview_text_edit, null) as EditText },
            { v, bs -> v.bind(bs, { it.toBigDecimalSafe() } )}
        )
        val oneWayRecipeFactory = ViewItemRecipeFactory<TextView, Observable<BigDecimal>>(
            { View.inflate(context, R.layout.tableview_text_view, null) as TextView },
            { v, observable -> v.bindIncoming(observable)}
        )
        reconcileVM.rowDatas
            .observeOn(AndroidSchedulers.mainThread())
            .observe(viewLifecycleOwner) { rowDatas ->
                myTableView_1.setRecipes(
                    listOf(
                        headerRecipeFactory.createOne2("Category")
                                + cellRecipeFactory.createOne2("Default")
                                + cellRecipeFactory.createMany(rowDatas.map { it.category.name }),
                        headerRecipeFactory_numbered.createOne2(Pair("Plan", planVM.stateExpectedIncome))
                                + oneWayRecipeFactory.createOne2(planVM.stateDifference)
                                + oneWayRecipeFactory.createMany(rowDatas.map { it.plan }),
                        headerRecipeFactory.createOne2("Actual")
                                + cellRecipeFactory.createOne2("")
                                + oneWayRecipeFactory.createMany(rowDatas.map { it.actual }),
                        headerRecipeFactory.createOne2("Reconcile")
                                + oneWayRecipeFactory.createOne2(reconcileVM.reconcileDefault)
                                + twoWayRecipeFactory.createMany(rowDatas.map { it.reconcile }),
                        headerRecipeFactory_numbered.createOne2(Pair("Budgeted",accountsVM.accountsTotal))
                                + oneWayRecipeFactory.createOne2(reconcileVM.uncategorizedBudgeted)
                                + oneWayRecipeFactory.createMany(rowDatas.map { it.budgeted })
                    ).reflectXY()
                )
            }
    }
}
