package com.tminus1010.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.jakewharton.rxbinding4.view.clicks
import com.tminus1010.budgetvalue.App
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.extensions.activityViewModels2
import com.tminus1010.budgetvalue.layer_ui.TMTableView.*
import com.tminus1010.budgetvalue.layer_ui.misc.bindIncoming
import com.tminus1010.budgetvalue.layer_ui.misc.bindOutgoing
import com.tminus1010.budgetvalue.model_app.Category
import com.tminus1010.budgetvalue.reflectXY
import com.tminus1010.budgetvalue.toBigDecimalSafe
import com.tminus1010.tmcommonkotlin_rx.observe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.frag_reconcile.*
import kotlinx.android.synthetic.main.tableview_header_income.view.*
import java.math.BigDecimal

class ReconcileFrag : Fragment(R.layout.frag_reconcile) {
    val app by lazy { requireActivity().application as App }
    val repo by lazy { app.appComponent.getRepo() }
    val transactionsVM: TransactionsVM by activityViewModels2 { TransactionsVM(repo, app.appComponent.getDatePeriodGetter()) }
    val accountsVM: AccountsVM by activityViewModels2 { AccountsVM(repo) }
    val categoriesAppVM by lazy { app.appComponent.getCategoriesAppVM() }
    val planVM: PlanVM by activityViewModels2 { PlanVM(repo, categoriesAppVM) }
    val activeReconciliationVM: ActiveReconciliationVM by activityViewModels2 { ActiveReconciliationVM(repo, transactionsVM.spends, accountsVM.accountsTotal, planVM) }
    val budgetedVM: BudgetedVM by activityViewModels2 { BudgetedVM(repo, transactionsVM, activeReconciliationVM) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBinds()
        budgetedVM
    }

    fun setupBinds() {
        // # Save Button
        btn_save.clicks().subscribe(activeReconciliationVM.intentSaveReconciliation)
        // # Table
        val cellRecipeFactory = ViewItemRecipeFactory.createCellRecipeFactory(requireContext())
        val headerRecipeFactory = ViewItemRecipeFactory.createHeaderRecipeFactory(requireContext())
        val headerRecipeFactory_numbered = ViewItemRecipeFactory<LinearLayout, Pair<String, Observable<BigDecimal>>>(
            { View.inflate(requireContext(), R.layout.tableview_header_income, null) as LinearLayout },
            { view, d ->
                view.textview_header.text = d.first
                view.textview_number.bindIncoming(d.second)
            })
        val reconcileCARecipeFactory = ViewItemRecipeFactory<EditText, Pair<Category, Observable<BigDecimal>>>(
            { View.inflate(context, R.layout.tableview_text_edit, null) as EditText },
            { view, (category, d) ->
                view.bindIncoming(d)
                view.bindOutgoing(activeReconciliationVM.intentPushActiveReconcileCA, { s -> category to s.toBigDecimalSafe() }) { it.second }
            }
        )
        val oneWayRecipeFactory = ViewItemRecipeFactory<TextView, Observable<BigDecimal>>(
            { View.inflate(context, R.layout.tableview_text_view, null) as TextView },
            { view, d -> view.bindIncoming(d) }
        )
        activeReconciliationVM.rowDatas
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(AndroidSchedulers.mainThread())
            .observe(viewLifecycleOwner) { rowDatas ->
                myTableView_1.setRecipes(
                    listOf(
                        headerRecipeFactory.createOne2("Category")
                                + cellRecipeFactory.createOne2("Default")
                                + cellRecipeFactory.createMany(rowDatas.map { it.category.name }),
                        headerRecipeFactory_numbered.createOne2(Pair("Plan", planVM.expectedIncome))
                                + oneWayRecipeFactory.createOne2(planVM.defaultAmount)
                                + oneWayRecipeFactory.createMany(rowDatas.map { it.plan }),
                        headerRecipeFactory.createOne2("Actual")
                                + cellRecipeFactory.createOne2("")
                                + oneWayRecipeFactory.createMany(rowDatas.map { it.actual }),
                        headerRecipeFactory.createOne2("Reconcile")
                                + oneWayRecipeFactory.createOne2(activeReconciliationVM.defaultAmount)
                                + reconcileCARecipeFactory.createMany(rowDatas.map { it.category to it.reconcile }),
                        headerRecipeFactory_numbered.createOne2(Pair("Budgeted",accountsVM.accountsTotal))
                                + oneWayRecipeFactory.createOne2(activeReconciliationVM.budgetedUncategorized)
                                + oneWayRecipeFactory.createMany(rowDatas.map { it.budgeted })
                    ).reflectXY()
                )
            }
    }
}
