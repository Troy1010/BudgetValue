package com.tminus1010.budgetvalue.reconciliations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tminus1010.budgetvalue.*
import com.tminus1010.budgetvalue._core.extensions.bind
import com.tminus1010.budgetvalue._core.extensions.easyText
import com.tminus1010.budgetvalue._core.extensions.getColorByAttr
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.view.onDone
import com.tminus1010.budgetvalue._core.middleware.view.tmTableView3.ViewItemRecipeFactory3
import com.tminus1010.budgetvalue._core.middleware.view.tmTableView3.recipeFactories
import com.tminus1010.budgetvalue._core.middleware.view.viewBinding
import com.tminus1010.budgetvalue.all.presentation_and_view.import_z.AccountsVM
import com.tminus1010.budgetvalue.budgeted.BudgetedVM
import com.tminus1010.budgetvalue.categories.CategoriesVM
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.databinding.*
import com.tminus1010.budgetvalue.plans.ActivePlanVM
import com.tminus1010.budgetvalue.transactions.TransactionsMiscVM
import com.tminus1010.tmcommonkotlin.core.extensions.reflectXY
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class AnytimeReconciliationFrag : Fragment(R.layout.frag_reconcile) {
    private val vb by viewBinding(FragReconcileBinding::bind)
    private val activeReconciliationVM: ActiveReconciliationVM by activityViewModels()
    private val categoriesVM: CategoriesVM by activityViewModels()
    private val activePlanVM: ActivePlanVM by activityViewModels()
    private val transactionsMiscVM: TransactionsMiscVM by activityViewModels()
    private val accountsVM: AccountsVM by activityViewModels()
    private val budgetedVM: BudgetedVM by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Bind Incoming from Presentation layer
        // ## State
        vb.buttonsview.buttons = activeReconciliationVM.buttons
        // ## TMTableView
        val numberedHeaderRecipeFactory = ViewItemRecipeFactory3<ItemHeaderIncomeBinding, Pair<String, Observable<String>>>(
            { ItemHeaderIncomeBinding.inflate(LayoutInflater.from(context)) },
            { d, vb, lifecycle ->
                vb.textviewHeader.text = d.first
                d.second.observe(lifecycle) { vb.textviewNumber.text = it }
            }
        )
        val reconcileCARecipeFactory = ViewItemRecipeFactory3<ItemMoneyEditTextBinding, Pair<Category, Observable<String>?>>(
            { ItemMoneyEditTextBinding.inflate(LayoutInflater.from(context)) },
            { (category, d), vb, lifecycle ->
                vb.moneyedittext.onDone { activeReconciliationVM.pushActiveReconcileCA(category, it) }
                if (d == null) return@ViewItemRecipeFactory3
                d.observe(lifecycle) { vb.moneyedittext.easyText = it }
            }
        )
        val budgetedRecipeFactory = ViewItemRecipeFactory3<ItemTextViewBinding, Observable<BigDecimal>?>(
            { ItemTextViewBinding.inflate(LayoutInflater.from(requireContext())) },
            { d, vb, lifecycle ->
                if (d == null) return@ViewItemRecipeFactory3
                vb.textview.bind(d, lifecycle) {
                    easyText = it.toString()
                    if (it < BigDecimal.ZERO)
                        setTextColor(context.theme.getColorByAttr(R.attr.colorOnError))
                    else
                        setTextColor(context.theme.getColorByAttr(R.attr.colorOnBackground))
                }
            },
        )
        Rx.combineLatest(categoriesVM.userCategories, activePlanVM.activePlanCAs, transactionsMiscVM.currentSpendBlockCAs, activeReconciliationVM.activeReconcileCAsToShow, budgetedVM.categoryAmounts)
            .observeOn(Schedulers.computation())
            .debounce(100, TimeUnit.MILLISECONDS)
            .map { (categories, activePlanCAs, currentSpendBlockCAs, activeReconciliationCAs, budgetedCA) ->
                val recipeGrid = listOf(
                    listOf(recipeFactories.header.createOne("Category"))
                            + recipeFactories.textView.createOne("Default")
                            + recipeFactories.textView.createMany(categories.map { it.name }),
                    listOf(numberedHeaderRecipeFactory.createOne(Pair("Plan", activePlanVM.expectedIncome)))
                            + recipeFactories.textViewWithLifecycle.createOne(activePlanVM.defaultAmount)
                            + recipeFactories.textViewWithLifecycle.createMany(categories.map { activePlanCAs[it] }),
                    listOf(recipeFactories.header.createOne("Actual"))
                            + recipeFactories.textView.createOne("")
                            + recipeFactories.textView.createMany(categories.map { currentSpendBlockCAs[it]?.toString() ?: "" }),
                    listOf(recipeFactories.header.createOne("Reconcile"))
                            + recipeFactories.textViewWithLifecycle.createOne(activeReconciliationVM.defaultAmount)
                            + reconcileCARecipeFactory.createMany(categories.map { it to activeReconciliationCAs[it] }),
                    listOf(numberedHeaderRecipeFactory.createOne(Pair("Budgeted", accountsVM.accountsTotal)))
                            + recipeFactories.textViewWithLifecycle.createOne(budgetedVM.defaultAmount)
                            + budgetedRecipeFactory.createMany(categories.map { budgetedCA[it] })
                ).reflectXY()
                val dividerMap = categories
                    .withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to recipeFactories.titledDivider.createOne(it.value.type.name) }
                    .mapKeys { it.key + 2 } // header row, default row
                Pair(recipeGrid, dividerMap)
            }
            .observe(viewLifecycleOwner) { (recipeGrid, dividerMap) ->
                vb.tmTableView.initialize(
                    recipeGrid = recipeGrid,
                    shouldFitItemWidthsInsideTable = true,
                    dividerMap = dividerMap,
                    rowFreezeCount = 1,
                )
            }
    }
}
