package com.tminus1010.budgetvalue.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.extensions.show
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.reflectXY
import com.tminus1010.budgetvalue._core.middleware.ui.MenuItemPartial
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.ViewItemRecipeFactory3
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.itemHeaderBindingRF
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.itemTextViewBindingRF
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.itemTitledDividerBindingRF
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue._shared.date_period_getter.DatePeriodGetter
import com.tminus1010.budgetvalue.databinding.FragHistoryBinding
import com.tminus1010.budgetvalue.databinding.ItemHeaderWithSubtitleBinding
import com.tminus1010.budgetvalue.history.models.IHistoryColumnData
import com.tminus1010.budgetvalue.plans.PlansVM
import com.tminus1010.budgetvalue.plans.models.Plan
import com.tminus1010.budgetvalue.reconciliations.ReconciliationsVM
import com.tminus1010.budgetvalue.reconciliations.models.Reconciliation
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.AndroidEntryPoint
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
        val columnHeaderFactory = ViewItemRecipeFactory3(
            { ItemHeaderWithSubtitleBinding.inflate(LayoutInflater.from(requireContext())) },
            { d: IHistoryColumnData, vb, lifecycle ->
                vb.textviewHeader.text = d.title
                vb.textviewSubtitle.text = d.subTitle(datePeriodGetter)
                vb.root.setOnLongClickListener {
                    listOfNotNull(
                        when {
                            d is Plan && !d.isCurrent(datePeriodGetter) ->
                                MenuItemPartial("Delete") { plansVM.deletePlan(d) }
                            d is Reconciliation ->
                                MenuItemPartial("Delete") { reconciliationsVM.delete(d) }
                            else -> null
                        },
                    ).also { PopupMenu(requireActivity(), vb.root).show(it) }
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
                        listOf(itemHeaderBindingRF.createOne("Categories")) +
                                itemTextViewBindingRF.createOne("Default") +
                                itemTextViewBindingRF.createMany(activeCategories.map { it.name }),
                        *historyColumnDatas.map { historyColumnData ->
                            listOf(columnHeaderFactory.createOne(historyColumnData)) +
                                    itemTextViewBindingRF.createOne(historyColumnData.defaultAmount.toString()) +
                                    itemTextViewBindingRF.createMany(activeCategories.map { historyColumnData.categoryAmounts[it]?.toString() ?: "" })
                        }.toTypedArray()
                    ).reflectXY()
                val dividerMap = activeCategories
                    .withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to itemTitledDividerBindingRF.createOne(it.value.type.name) }
                    .mapKeys { it.key + 2 } // header row and default row
                Pair(recipe2D, dividerMap)
            }
            .observe(viewLifecycleOwner) { (recipe2D, dividerMap) ->
                vb.tmTableViewHistory.initialize(recipe2D, false, dividerMap, 1, 1)
            }
    }
}