package com.tminus1010.budgetvalue.reconciliations

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.jakewharton.rxbinding4.view.clicks
import com.tminus1010.budgetvalue.*
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.reflectXY
import com.tminus1010.budgetvalue._core.middleware.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.middleware.ui.bindIncoming
import com.tminus1010.budgetvalue._core.middleware.ui.bindOutgoing
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView.ViewItemRecipeFactory
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue.accounts.AccountsVM
import com.tminus1010.budgetvalue.budgeted.BudgetedVM
import com.tminus1010.budgetvalue.categories.CategoriesVM
import com.tminus1010.budgetvalue.databinding.FragReconcileBinding
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.databinding.ItemHeaderIncomeBinding
import com.tminus1010.budgetvalue.plans.ActivePlanVM
import com.tminus1010.budgetvalue.transactions.TransactionsVM
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class ReconcileFrag : Fragment(R.layout.frag_reconcile) {
    val activeReconciliationVM2 by activityViewModels<ActiveReconciliationVM2>()
    val activeReconciliationVM by activityViewModels<ActiveReconciliationVM>()
    val categoriesVM by activityViewModels<CategoriesVM>()
    val activePlanVM by activityViewModels<ActivePlanVM>()
    val transactionsVM by activityViewModels<TransactionsVM>()
    val accountsVM by activityViewModels<AccountsVM>()
    val budgetedVM by activityViewModels<BudgetedVM>()
    val vb by viewBinding(FragReconcileBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Init VMs
        // Hilt requires that VM initializations are on main thread.
        activeReconciliationVM2
        // # Clicks
        vb.btnSave.clicks().observe(viewLifecycleOwner) {
            activeReconciliationVM2.intentSaveReconciliation.onNext(Unit)
        }
        // # TMTableView
        val cellRecipeFactory = ViewItemRecipeFactory.createCellRecipeFactory(requireContext())
        val headerRecipeFactory = ViewItemRecipeFactory.createHeaderRecipeFactory(requireContext())
        val headerRecipeFactory_numbered = ViewItemRecipeFactory<LinearLayout, Pair<String, Observable<BigDecimal>>>(
            { View.inflate(requireContext(), R.layout.item_header_income, null) as LinearLayout },
            { v, d ->
                val binding = ItemHeaderIncomeBinding.bind(v)
                binding.textviewHeader.text = d.first
                binding.textviewNumber.bindIncoming(d.second)
            })
        val reconcileCARecipeFactory = ViewItemRecipeFactory<EditText, Pair<Category, Observable<BigDecimal>?>>(
            { View.inflate(context, R.layout.item_text_edit, null) as EditText },
            { v, (category, d) ->
                if (d==null) return@ViewItemRecipeFactory
                v.bindIncoming(d)
                v.bindOutgoing(activeReconciliationVM.intentPushActiveReconcileCA, { s -> category to s.toMoneyBigDecimal() }) { it.second }
            }
        )
        val cellRecipeFactory2 = ViewItemRecipeFactory(
            { View.inflate(context, R.layout.item_text_view, null) as TextView },
            { v: TextView, d: Any? -> v.text = d?.toString() }
        )
        val oneWayRecipeFactory = ViewItemRecipeFactory<TextView, Observable<BigDecimal>?>(
            { View.inflate(context, R.layout.item_text_view, null) as TextView },
            { v, d -> if (d!=null) v.bindIncoming(d) }
        )
        val titledDividerRecipeFactory = ViewItemRecipeFactory<TextView, String>(
            { View.inflate(context, R.layout.item_titled_divider, null) as TextView },
            { v, s -> v.text = s }
        )
        Rx.combineLatest(categoriesVM.userCategories, activePlanVM.activePlanCAs, transactionsVM.currentSpendBlockCAs, activeReconciliationVM.activeReconcileCAs2, budgetedVM.categoryAmountsObservableMap)
            .observeOn(Schedulers.computation())
            .debounce(100, TimeUnit.MILLISECONDS)
            .map { (categories, activePlanCAs, currentSpendBlockCAs, activeReconciliationCAs, budgetedCA) ->
                val recipeGrid = listOf(
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
                ).reflectXY()
                val dividerMap = categories
                    .withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to titledDividerRecipeFactory.createOne(it.value.type.name) }
                    .mapKeys { it.key + 2 } // header row, default row
                Pair(recipeGrid, dividerMap)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .observe(viewLifecycleOwner) { (recipeGrid, dividerMap) ->
                vb.myTableView1.initialize(
                    recipeGrid = recipeGrid,
                    shouldFitItemWidthsInsideTable = true,
                    dividerMap = dividerMap,
                    colFreezeCount = 0,
                    rowFreezeCount = 1
                )
            }
    }
}
