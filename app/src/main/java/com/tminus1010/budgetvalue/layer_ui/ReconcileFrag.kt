package com.tminus1010.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.jakewharton.rxbinding4.view.clicks
import com.tminus1010.budgetvalue.*
import com.tminus1010.budgetvalue.databinding.FragReconcileBinding
import com.tminus1010.budgetvalue.databinding.TableviewHeaderIncomeBinding
import com.tminus1010.budgetvalue.dependency_injection.ViewModelProviders
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.appComponent
import com.tminus1010.budgetvalue.extensions.itemObservableMap2
import com.tminus1010.budgetvalue.middleware.ui.TMTableView.ViewItemRecipeFactory
import com.tminus1010.budgetvalue.middleware.ui.bindIncoming
import com.tminus1010.budgetvalue.middleware.ui.bindOutgoing
import com.tminus1010.budgetvalue.middleware.ui.viewBinding
import com.tminus1010.budgetvalue.features.categories.Category
import com.tminus1010.budgetvalue.features_shared.IViewModels
import com.tminus1010.budgetvalue.middleware.Rx
import com.tminus1010.budgetvalue.middleware.reflectXY
import com.tminus1010.budgetvalue.middleware.toMoneyBigDecimal
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class ReconcileFrag : Fragment(R.layout.frag_reconcile), IViewModels {
    val binding by viewBinding(FragReconcileBinding::bind)
    override val viewModelProviders by lazy { ViewModelProviders(requireActivity(), appComponent) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Clicks
        binding.btnSave.clicks().observe(viewLifecycleOwner) {
            activeReconciliationVM2.intentSaveReconciliation.onNext(Unit)
        }
        // # TMTableView
        val cellRecipeFactory = ViewItemRecipeFactory.createCellRecipeFactory(requireContext())
        val headerRecipeFactory = ViewItemRecipeFactory.createHeaderRecipeFactory(requireContext())
        val headerRecipeFactory_numbered = ViewItemRecipeFactory<LinearLayout, Pair<String, Observable<BigDecimal>>>(
            { View.inflate(requireContext(), R.layout.tableview_header_income, null) as LinearLayout },
            { v, d ->
                val binding = TableviewHeaderIncomeBinding.bind(v)
                binding.textviewHeader.text = d.first
                binding.textviewNumber.bindIncoming(d.second)
            })
        val reconcileCARecipeFactory = ViewItemRecipeFactory<EditText, Pair<Category, Observable<BigDecimal>?>>(
            { View.inflate(context, R.layout.tableview_text_edit, null) as EditText },
            { v, (category, d) ->
                if (d==null) return@ViewItemRecipeFactory
                v.bindIncoming(d)
                v.bindOutgoing(activeReconciliationVM.intentPushActiveReconcileCA, { s -> category to s.toMoneyBigDecimal() }) { it.second }
            }
        )
        val cellRecipeFactory2 = ViewItemRecipeFactory(
            { View.inflate(context, R.layout.tableview_text_view, null) as TextView },
            { v: TextView, d: Any? -> v.text = d?.toString() }
        )
        val oneWayRecipeFactory = ViewItemRecipeFactory<TextView, Observable<BigDecimal>?>(
            { View.inflate(context, R.layout.tableview_text_view, null) as TextView },
            { v, d -> if (d!=null) v.bindIncoming(d) }
        )
        val titledDividerRecipeFactory = ViewItemRecipeFactory<TextView, String>(
            { View.inflate(context, R.layout.tableview_titled_divider, null) as TextView },
            { v, s -> v.text = s }
        )
        Rx.combineLatest(categoriesVM.userCategories, activePlanVM.activePlanCAs.itemObservableMap2(), transactionsVM.currentSpendBlockCAs, activeReconciliationVM.activeReconcileCAs.value.itemObservableMap2, budgetedVM.categoryAmounts.value.itemObservableMap2)
            .observeOn(AndroidSchedulers.mainThread())
            .debounce(100, TimeUnit.MILLISECONDS) // budgetedCA[it.category]!! causes null pointer exception without this
            .observe(viewLifecycleOwner) { (categories, activePlanCAs, currentSpendBlockCAs, activeReconciliationCAs, budgetedCA) ->
                val dividerMap = categories
                    .withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to titledDividerRecipeFactory.createOne(it.value.type.name) }
                    .mapKeys { it.key + 2 } // header row, default row
                binding.myTableView1.initialize(
                    recipeGrid = listOf(
                        headerRecipeFactory.createOne2("Category")
                                + cellRecipeFactory.createOne2("Default")
                                + cellRecipeFactory.createMany(categories.map { it.name }),
                        headerRecipeFactory_numbered.createOne2(Pair("Plan", activePlanVM.expectedIncome))
                                + oneWayRecipeFactory.createOne2(activePlanVM.defaultAmount)
                                + oneWayRecipeFactory.createMany(categories.map { activePlanCAs[it] }),
                        headerRecipeFactory.createOne2("Actual")
                                + cellRecipeFactory2.createOne2("")
                                + cellRecipeFactory2.createMany(categories.map { currentSpendBlockCAs[it] ?: BigDecimal.ZERO }),
                        headerRecipeFactory.createOne2("Reconcile")
                                + oneWayRecipeFactory.createOne2(activeReconciliationVM2.defaultAmount)
                                + reconcileCARecipeFactory.createMany(categories.map { it to activeReconciliationCAs[it] }),
                        headerRecipeFactory_numbered.createOne2(Pair("Budgeted", accountsVM.accountsTotal))
                                + oneWayRecipeFactory.createOne2(budgetedVM.defaultAmount)
                                + oneWayRecipeFactory.createMany(categories.map { budgetedCA[it] })
                    ).reflectXY(),
                    shouldFitItemWidthsInsideTable = true,
                    dividerMap = dividerMap,
                    colFreezeCount = 0,
                    rowFreezeCount = 1
                )
            }
    }
}
