package com.example.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.budgetvalue.*
import com.example.budgetvalue.layer_ui.TMTableView.IViewItemRecipe
import com.example.budgetvalue.layer_ui.TMTableView.ViewItemRecipeFactory
import com.example.budgetvalue.layer_ui.misc.bindIncoming
import com.example.budgetvalue.layer_ui.misc.bindOutgoing
import com.example.budgetvalue.model_app.Category
import com.tminus1010.tmcommonkotlin.misc.createVmFactory
import com.tminus1010.tmcommonkotlin_rx.observe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.Subject
import kotlinx.android.synthetic.main.frag_history.*
import java.math.BigDecimal

class HistoryFrag : Fragment(R.layout.frag_history) {
    val app by lazy { requireActivity().application as App }
    val repo by lazy { app.appComponent.getRepo() }
    val transactionsVM: TransactionsVM by activityViewModels { createVmFactory { TransactionsVM(repo) } }
    val accountsVM: AccountsVM by activityViewModels { createVmFactory { AccountsVM(repo) } }
    val categoriesAppVM by lazy { app.appComponent.getCategoriesAppVM() }
    val planVM: PlanVM by activityViewModels { createVmFactory { PlanVM(repo, categoriesAppVM) } }
    val reconcileVM: ReconcileVM by activityViewModels {
        createVmFactory {
            ReconcileVM(repo,
                transactionsVM.spends,
                accountsVM.accountsTotal,
                planVM)
        }
    }
    val datePeriodGetter by lazy { app.appComponent.getDatePeriodGetter() }
    val historyVM: HistoryVM by activityViewModels {
        createVmFactory {
            HistoryVM(transactionsVM,
                reconcileVM,
                planVM,
                datePeriodGetter)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        val cellRecipeFactory = ViewItemRecipeFactory.createCellRecipeFactory(requireContext())
        val headerRecipeFactory = ViewItemRecipeFactory.createHeaderRecipeFactory(requireContext())
        val inputRecipeFactory =
            ViewItemRecipeFactory<EditText, Pair<BigDecimal, Subject<BigDecimal>>>(
                { View.inflate(context, R.layout.tableview_text_edit, null) as EditText },
                { v, (state, actionSubject) ->
                    v.setText("$state"); v.bindOutgoing(actionSubject,
                    { it.toBigDecimalSafe() })
                }
            )
        val inputRecipeFactory2 =
            ViewItemRecipeFactory<EditText, Pair<Map.Entry<Category, BigDecimal>, Subject<Pair<Category, BigDecimal>>>>(
                { View.inflate(context, R.layout.tableview_text_edit, null) as EditText },
                { v, (kv, actionSubject) ->
                    v.setText("${kv.value}"); v.bindOutgoing(actionSubject,
                    { Pair(kv.key, it.toBigDecimalSafe()) })
                }
            )
        val oneWayRecipeFactory = ViewItemRecipeFactory<TextView, Observable<BigDecimal>>(
            { View.inflate(context, R.layout.tableview_text_view, null) as TextView },
            { v, bs -> v.bindIncoming(bs) }
        )
        val titledDividerRecipeFactory = ViewItemRecipeFactory<TextView, String>(
            { View.inflate(context, R.layout.tableview_titled_divider, null) as TextView },
            { v, s -> v.text = s }
        )
        combineLatestAsTuple(historyVM.stateHistoryColumnDatas, historyVM.activeCategories)
            .observeOn(AndroidSchedulers.mainThread())
            .distinctUntilChanged() //*idk why this emitted a copy without distinctUntilChanged
            .observe(viewLifecycleOwner) { (historyColumnDatas, activeCategories) ->
                // # setRecipes
                tmTableView_history.setRecipes(
                    arrayListOf<Iterable<IViewItemRecipe>>(
                        headerRecipeFactory.createOne2("Categories") +
                                cellRecipeFactory.createMany(activeCategories.map { it.name })
                    )
                        .apply {
                            addAll(
                                historyColumnDatas.map {
                                    headerRecipeFactory.createOne2(it.title) +
                                            cellRecipeFactory.createMany(activeCategories.map { k ->
                                                it.categoryAmounts[k]?.toString() ?: ""
                                            })
                                }
                            )
                        }.reflectXY()
                )
                // # setSeparators
                tmTableView_history.setDiviers(mapOf(3 to titledDividerRecipeFactory.createOne("CATEGORY A")))
            }
    }
}