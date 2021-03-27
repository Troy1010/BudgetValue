package com.tminus1010.budgetvalue.history

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.dependency_injection.ViewModelProviders
import com.tminus1010.budgetvalue._core.dependency_injection.injection_extensions.appComponent
import com.tminus1010.budgetvalue._core.dependency_injection.injection_extensions.domain
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.reflectXY
import com.tminus1010.budgetvalue._core.middleware.ui.MenuItemPartial
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView.ViewItemRecipeFactory
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue._layer_facades.IViewModels
import com.tminus1010.budgetvalue.databinding.FragHistoryBinding
import com.tminus1010.budgetvalue.extensions.show
import com.tminus1010.budgetvalue.plans.Plan
import com.tminus1010.budgetvalue.reconciliations.Reconciliation
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class HistoryFrag : Fragment(R.layout.frag_history), IViewModels {
    override val viewModelProviders by lazy { ViewModelProviders(requireActivity(), appComponent) }
    val vb by viewBinding(FragHistoryBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # TMTableView
        val cellRecipeFactory = ViewItemRecipeFactory.createCellRecipeFactory(requireContext())
        val headerRecipeFactory = ViewItemRecipeFactory.createHeaderRecipeFactory(requireContext())
        val columnHeaderFactory = ViewItemRecipeFactory<LinearLayout, IHistoryColumnData>(
            { View.inflate(context, R.layout.tableview_header_with_subtitle, null) as LinearLayout }, // TODO("use viewBinding")
            { v, historyColumnData ->
                (v.children.first() as TextView).text = historyColumnData.title
                (v.children.last() as TextView).text = historyColumnData.subTitle(domain)
                v.setOnLongClickListener {
                    listOfNotNull(
                        when {
                            historyColumnData is Plan && !historyColumnData.isCurrent(domain) -> {
                                { plansVM.intentDeletePlan.onNext(historyColumnData) }
                            }
                            historyColumnData is Reconciliation -> {
                                { reconciliationsVM.intentDeleteReconciliation.onNext(historyColumnData) }
                            }
                            else -> null
                        }?.let { MenuItemPartial("Delete", it) })
                        .also { PopupMenu(requireActivity(), v).show(it) }
                    true
                }
            },
        )
        val titledDividerRecipeFactory = ViewItemRecipeFactory<TextView, String>(
            { View.inflate(context, R.layout.tableview_titled_divider, null) as TextView },
            { v, s -> v.text = s }
        )
        Rx.combineLatest(historyVM.historyColumnDatas, historyVM.activeCategories)
            .distinctUntilChanged() //*idk why this emitted a copy without distinctUntilChanged
            .observeOn(Schedulers.computation())
            .map { (historyColumnDatas, activeCategories) ->
                val recipe2D =
                    listOf(
                        headerRecipeFactory.createOne2("Categories") +
                                cellRecipeFactory.createOne("Default") +
                                cellRecipeFactory.createMany(activeCategories.map { it.name }),
                        *historyColumnDatas.map { historyColumnData ->
                            columnHeaderFactory.createOne2(historyColumnData) +
                                    cellRecipeFactory.createOne(historyColumnData.defaultAmount.toString()) +
                                    cellRecipeFactory.createMany(activeCategories.map { historyColumnData.categoryAmounts[it]?.toString() ?: "" })
                        }.toTypedArray()
                    ).reflectXY()
                val dividerMap = activeCategories
                    .withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to titledDividerRecipeFactory.createOne(it.value.type.name) }
                    .mapKeys { it.key + 2 } // header row and default row
                Pair(recipe2D, dividerMap)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .observe(viewLifecycleOwner) { (recipe2D, dividerMap) ->
                vb.tmTableViewHistory.initialize(recipe2D, false, dividerMap, 1, 1)
            }
    }
}