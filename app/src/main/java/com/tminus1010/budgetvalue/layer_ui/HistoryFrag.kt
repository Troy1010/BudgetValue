package com.tminus1010.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.App
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.combineLatestAsTuple
import com.tminus1010.budgetvalue.extensions.activityViewModels2
import com.tminus1010.budgetvalue.extensions.distinctUntilChangedWith
import com.tminus1010.budgetvalue.layer_ui.TMTableView.IViewItemRecipe
import com.tminus1010.budgetvalue.layer_ui.TMTableView.ViewItemRecipeFactory
import com.tminus1010.budgetvalue.layer_ui.TMTableView2.RecipeGrid
import com.tminus1010.budgetvalue.reflectXY
import com.tminus1010.tmcommonkotlin_rx.observe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.frag_history.*

class HistoryFrag : Fragment(R.layout.frag_history) {
    val app by lazy { requireActivity().application as App }
    val repo by lazy { app.appComponent.getRepo() }
    val transactionsVM: TransactionsVM by activityViewModels2 { TransactionsVM(repo, app.appComponent.getDatePeriodGetter()) }
    val accountsVM: AccountsVM by activityViewModels2 { AccountsVM(repo) }
    val categoriesAppVM by lazy { app.appComponent.getCategoriesAppVM() }
    val planVM: PlanVM by activityViewModels2 { PlanVM(repo, categoriesAppVM) }
    val activeReconciliationVM: ActiveReconciliationVM by activityViewModels2 {
        ActiveReconciliationVM(repo, transactionsVM.spends, accountsVM.accountsTotal, planVM)
    }
    val datePeriodGetter by lazy { app.appComponent.getDatePeriodGetter() }
    val historyVM: HistoryVM by activityViewModels2 {
        HistoryVM(repo, transactionsVM, activeReconciliationVM, planVM, datePeriodGetter)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBinds()
    }

    private fun setupBinds() {
        val cellRecipeFactory = ViewItemRecipeFactory.createCellRecipeFactory(requireContext())
        val headerRecipeFactory = ViewItemRecipeFactory.createHeaderRecipeFactory(requireContext())
        val doubleHeaderRecipeFactory = ViewItemRecipeFactory<LinearLayout, Pair<Any, Any?>>(
            {
                View.inflate(context, R.layout.tableview_header_with_subtitle, null) as LinearLayout
            },
            { v, pair ->
                (v.children.first() as TextView).text = pair.first.toString()
                if (pair.second != null)
                    (v.children.last() as TextView).text = pair.second.toString()
            },
        )
        val titledDividerRecipeFactory = ViewItemRecipeFactory<TextView, String>(
            { View.inflate(context, R.layout.tableview_titled_divider, null) as TextView },
            { v, s -> v.text = s }
        )
        combineLatestAsTuple(historyVM.historyColumnDatas, historyVM.activeCategories)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(AndroidSchedulers.mainThread())
            .distinctUntilChanged() //*idk why this emitted a copy without distinctUntilChanged
            .observe(viewLifecycleOwner) { (historyColumnDatas, activeCategories) ->
                val recipe2D = RecipeGrid(
                    arrayListOf<List<IViewItemRecipe>>(
                        headerRecipeFactory.createOne2("Categories") +
                                cellRecipeFactory.createOne("Default") +
                                cellRecipeFactory.createMany(activeCategories.map { it.name })
                    ).apply {
                        addAll(
                            historyColumnDatas.map {
                                doubleHeaderRecipeFactory.createOne2(Pair(it.title, it.subTitle)) +
                                        cellRecipeFactory.createOne(it.defaultAmount.toString()) +
                                        cellRecipeFactory.createMany(activeCategories.map { k ->
                                            it.categoryAmounts[k]?.toString() ?: ""
                                        })
                            }
                        )
                    }.reflectXY())
                val dividerMap = activeCategories
                    .withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to titledDividerRecipeFactory.createOne(it.value.type.name) }
                    .mapKeys { it.key + 2 } // header row and default row
                tmTableView_history.initialize(recipe2D, dividerMap, 1, 1)
            }
    }
}