package com.tminus1010.budgetvalue.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.reflectXY
import com.tminus1010.budgetvalue._core.middleware.ui.MenuItemPartial
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView.ViewItemRecipeFactory
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue._shared.date_period_getter.DatePeriodGetter
import com.tminus1010.budgetvalue.databinding.FragHistoryBinding
import com.tminus1010.budgetvalue._core.extensions.show
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.ViewItemRecipeFactory3
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.viewItemRecipeFactories.ItemTitledDividerBindingRF
import com.tminus1010.budgetvalue._core.ui.data_binding.bindText
import com.tminus1010.budgetvalue.databinding.ItemTextViewBinding
import com.tminus1010.budgetvalue.history.models.IHistoryColumnData
import com.tminus1010.budgetvalue.plans.PlansVM
import com.tminus1010.budgetvalue.plans.models.Plan
import com.tminus1010.budgetvalue.reconciliations.ReconciliationsVM
import com.tminus1010.budgetvalue.reconciliations.models.Reconciliation
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@AndroidEntryPoint
class HistoryFrag : Fragment(R.layout.frag_history) {
    @Inject lateinit var datePeriodGetter: DatePeriodGetter
    val plansVM: PlansVM by activityViewModels()
    val reconciliationsVM: ReconciliationsVM by activityViewModels()
    val historyVM: HistoryVM by activityViewModels()
    val vb by viewBinding(FragHistoryBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # TMTableView
        val cellRecipeFactory = ViewItemRecipeFactory3(
            createVB = { ItemTextViewBinding.inflate(LayoutInflater.from(context)) },
            bind = { d: String, vb, _ ->
                vb.textviewBasicCell.text = d
            }
        )
        val cellRecipeFactory2 = ViewItemRecipeFactory3(
            createVB = { ItemTextViewBinding.inflate(LayoutInflater.from(context)) },
            bind = { d: LiveData<String>, vb, lifecycle ->
                vb.textviewBasicCell.bindText(d, lifecycle)
            }
        )
        val headerRecipeFactory = ViewItemRecipeFactory.createHeaderRecipeFactory(requireContext())
        val columnHeaderFactory = ViewItemRecipeFactory<LinearLayout, IHistoryColumnData>(
            { View.inflate(context, R.layout.item_header_with_subtitle, null) as LinearLayout }, // TODO("use viewBinding")
            { v, historyColumnData ->
                (v.children.first() as TextView).text = historyColumnData.title
                (v.children.last() as TextView).text = historyColumnData.subTitle(datePeriodGetter)
                v.setOnLongClickListener {
                    listOfNotNull(
                        when {
                            historyColumnData is Plan && !historyColumnData.isCurrent(datePeriodGetter) -> {
                                { plansVM.deletePlan(historyColumnData) }
                            }
                            historyColumnData is Reconciliation -> {
                                { reconciliationsVM.delete(historyColumnData) }
                            }
                            else -> null
                        }?.let { MenuItemPartial("Delete", it) })
                        .also { PopupMenu(requireActivity(), v).show(it) }
                    true
                }
            },
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
                    .associate { it.index to ItemTitledDividerBindingRF(requireContext()).createOne(it.value.type.name) }
                    .mapKeys { it.key + 2 } // header row and default row
                Pair(recipe2D, dividerMap)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .observe(viewLifecycleOwner) { (recipe2D, dividerMap) ->
                vb.tmTableViewHistory.initialize(recipe2D, false, dividerMap, 1, 1)
            }
    }
}