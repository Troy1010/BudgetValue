package com.tminus1010.budgetvalue.reconciliations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import com.tminus1010.budgetvalue.*
import com.tminus1010.budgetvalue._core.extensions.bind
import com.tminus1010.budgetvalue._core.extensions.easyText
import com.tminus1010.budgetvalue._core.extensions.toObservable
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.reflectXY
import com.tminus1010.budgetvalue._core.middleware.ui.onDone
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.ViewItemRecipeFactory3
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.recipeFactories
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue._core.ui.data_binding.bindText
import com.tminus1010.budgetvalue.accounts.AccountsVM
import com.tminus1010.budgetvalue.budgeted.BudgetedVM
import com.tminus1010.budgetvalue.categories.CategoriesVM
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.databinding.*
import com.tminus1010.budgetvalue.plans.ActivePlanVM
import com.tminus1010.budgetvalue.transactions.TransactionsVM
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class ReconcileFrag : Fragment(R.layout.frag_reconcile) {
    val activeReconciliationVM by activityViewModels<ActiveReconciliationVM>()
    val categoriesVM by activityViewModels<CategoriesVM>()
    val activePlanVM by activityViewModels<ActivePlanVM>()
    val transactionsVM by activityViewModels<TransactionsVM>()
    val accountsVM by activityViewModels<AccountsVM>()
    val budgetedVM by activityViewModels<BudgetedVM>()
    val vb by viewBinding(FragReconcileBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Clicks
        vb.btnSave.setOnClickListener { activeReconciliationVM.saveReconciliation() }
        // # TMTableView
        val cellRecipeFactory = ViewItemRecipeFactory3<ItemTextViewBinding, String>(
            { ItemTextViewBinding.inflate(LayoutInflater.from(context)) },
            { s, v, _ -> v.textview.text = s }
        )
        val headerRecipeFactory = ViewItemRecipeFactory3<ItemHeaderBinding, String>(
            { ItemHeaderBinding.inflate(LayoutInflater.from(context)) },
            { s, v, _ -> v.textview.text = s }
        )
        val headerRecipeFactory_numbered = ViewItemRecipeFactory3<ItemHeaderIncomeBinding, Pair<String, Observable<String>>>(
            { ItemHeaderIncomeBinding.inflate(LayoutInflater.from(context)) },
            { d, v, _ ->
                v.textviewHeader.text = d.first
                v.textviewNumber.bind(d.second, viewLifecycleOwner) { text = it }
            }
        )
        val reconcileCARecipeFactory = ViewItemRecipeFactory3<ItemTextEditBinding, Pair<Category, LiveData<String>?>>(
            { ItemTextEditBinding.inflate(LayoutInflater.from(context)) },
            { (category, d), v, _ ->
                if (d==null) return@ViewItemRecipeFactory3
                v.editText.bindText(d, viewLifecycleOwner)
                v.editText.onDone { activeReconciliationVM.pushActiveReconcileCA(category, it) }
            }
        )
        val cellRecipeFactory2 = ViewItemRecipeFactory3<ItemTextViewBinding, Any?>(
            { ItemTextViewBinding.inflate(LayoutInflater.from(context)) },
            { d, v, _ -> v.textview.text = d?.toString() }
        )
        val oneWayRecipeFactory2 = ViewItemRecipeFactory3<ItemTextViewBinding, Observable<String>?>(
            { ItemTextViewBinding.inflate(LayoutInflater.from(context),) },
            { d, v, lifecycle -> if (d != null) v.textview.bind(d, lifecycle) { easyText = it } }
        )
        Rx.combineLatest(categoriesVM.userCategories, activePlanVM.activePlanCAs, transactionsVM.currentSpendBlockCAs, activeReconciliationVM.activeReconcileCAs2, budgetedVM.categoryAmounts.toObservable(viewLifecycleOwner))
            .observeOn(Schedulers.computation())
            .debounce(100, TimeUnit.MILLISECONDS)
            .map { (categories, activePlanCAs, currentSpendBlockCAs, activeReconciliationCAs, budgetedCA) ->
                val recipeGrid = listOf(
                    listOf(headerRecipeFactory.createOne("Category"))
                            + cellRecipeFactory.createOne("Default")
                            + cellRecipeFactory.createMany(categories.map { it.name }),
                    listOf(headerRecipeFactory_numbered.createOne(Pair("Plan", activePlanVM.expectedIncome)))
                            + oneWayRecipeFactory2.createOne(activePlanVM.defaultAmount)
                            + oneWayRecipeFactory2.createMany(categories.map { activePlanCAs[it] }),
                    listOf(headerRecipeFactory.createOne("Actual"))
                            + cellRecipeFactory2.createOne("")
                            + cellRecipeFactory2.createMany(categories.map { currentSpendBlockCAs[it] ?: BigDecimal.ZERO }),
                    listOf(headerRecipeFactory.createOne("Reconcile"))
                            + oneWayRecipeFactory2.createOne(activeReconciliationVM.defaultAmount)
                            + reconcileCARecipeFactory.createMany(categories.map { it to activeReconciliationCAs[it] }),
                    listOf(headerRecipeFactory_numbered.createOne(Pair("Budgeted", accountsVM.accountsTotal)))
                            + oneWayRecipeFactory2.createOne(budgetedVM.defaultAmount)
                            + oneWayRecipeFactory2.createMany(categories.map { budgetedCA[it] })
                ).reflectXY()
                val dividerMap = categories
                    .withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to recipeFactories.titledDivider.createOne(it.value.type.name) }
                    .mapKeys { it.key + 2 } // header row, default row
                Pair(recipeGrid, dividerMap)
            }
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
