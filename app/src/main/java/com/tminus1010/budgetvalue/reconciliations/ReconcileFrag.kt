package com.tminus1010.budgetvalue.reconciliations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tminus1010.budgetvalue.*
import com.tminus1010.budgetvalue._core.extensions.easyText
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.reflectXY
import com.tminus1010.budgetvalue._core.middleware.ui.onDone
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.ViewItemRecipeFactory3
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.recipeFactories
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
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
    private val activeReconciliationVM by activityViewModels<ActiveReconciliationVM>()
    private val categoriesVM by activityViewModels<CategoriesVM>()
    private val activePlanVM by activityViewModels<ActivePlanVM>()
    private val transactionsVM by activityViewModels<TransactionsVM>()
    private val accountsVM by activityViewModels<AccountsVM>()
    private val budgetedVM by activityViewModels<BudgetedVM>()
    private val vb by viewBinding(FragReconcileBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Output
        vb.btnSave.setOnClickListener { activeReconciliationVM.saveReconciliation() }
        // # TMTableView
        val headerRecipeFactory_numbered = ViewItemRecipeFactory3<ItemHeaderIncomeBinding, Pair<String, Observable<String>>>(
            { ItemHeaderIncomeBinding.inflate(LayoutInflater.from(context)) },
            { d, vb, lifecycle ->
                vb.textviewHeader.text = d.first
                d.second.observe(lifecycle) { vb.textviewNumber.text = it }
            }
        )
        val reconcileCARecipeFactory = ViewItemRecipeFactory3<ItemTextEditBinding, Pair<Category, Observable<String>?>>(
            { ItemTextEditBinding.inflate(LayoutInflater.from(context)) },
            { (category, d), vb, lifecycle ->
                if (d==null) return@ViewItemRecipeFactory3
                d.observe(lifecycle) { vb.editText.easyText = it }
                vb.editText.onDone { activeReconciliationVM.pushActiveReconcileCA(category, it) }
            }
        )
        Rx.combineLatest(categoriesVM.userCategories, activePlanVM.activePlanCAs, transactionsVM.currentSpendBlockCAs, activeReconciliationVM.activeReconcileCAs2, budgetedVM.categoryAmounts)
            .observeOn(Schedulers.computation())
            .debounce(100, TimeUnit.MILLISECONDS)
            .map { (categories, activePlanCAs, currentSpendBlockCAs, activeReconciliationCAs, budgetedCA) ->
                val recipeGrid = listOf(
                    listOf(recipeFactories.header.createOne("Category"))
                            + recipeFactories.textView.createOne("Default")
                            + recipeFactories.textView.createMany(categories.map { it.name }),
                    listOf(headerRecipeFactory_numbered.createOne(Pair("Plan", activePlanVM.expectedIncome)))
                            + recipeFactories.textViewWithLifecycle.createOne(activePlanVM.defaultAmount)
                            + recipeFactories.textViewWithLifecycle.createMany(categories.map { activePlanCAs[it] }),
                    listOf(recipeFactories.header.createOne("Actual"))
                            + recipeFactories.textView.createOne("")
                            + recipeFactories.textView.createMany(categories.map { currentSpendBlockCAs[it] ?: BigDecimal.ZERO }),
                    listOf(recipeFactories.header.createOne("Reconcile"))
                            + recipeFactories.textViewWithLifecycle.createOne(activeReconciliationVM.defaultAmount)
                            + reconcileCARecipeFactory.createMany(categories.map { it to activeReconciliationCAs[it] }),
                    listOf(headerRecipeFactory_numbered.createOne(Pair("Budgeted", accountsVM.accountsTotal)))
                            + recipeFactories.textViewWithLifecycle.createOne(budgetedVM.defaultAmount)
                            + recipeFactories.textViewWithLifecycle.createMany(categories.map { budgetedCA[it] })
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
                    rowFreezeCount = 1,
                )
            }
    }
}
