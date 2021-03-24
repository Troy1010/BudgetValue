package com.tminus1010.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.databinding.FragHistoryBinding
import com.tminus1010.budgetvalue.dependency_injection.ViewModelProviders
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.appComponent
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.domain
import com.tminus1010.budgetvalue.features.history.IHistoryColumnData
import com.tminus1010.budgetvalue.middleware.Rx
import com.tminus1010.budgetvalue.middleware.reflectXY
import com.tminus1010.budgetvalue.middleware.ui.tmTableView.ViewItemRecipeFactory
import com.tminus1010.budgetvalue.middleware.ui.viewBinding
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

class HistoryFrag : Fragment(R.layout.frag_history), IHostFragChild, IViewModels {
    override val viewModelProviders by lazy { ViewModelProviders(requireActivity(), appComponent) }
    val binding by viewBinding(FragHistoryBinding::bind)
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
                    PopupMenu(requireActivity(), v).apply {
                        inflate(R.menu.history_column_menu)
                        setOnMenuItemClickListener {
                            when(it.itemId) {
                                R.id.delete -> TODO()
                                else -> TODO()
                            }
                        }
                        show()
                    }
                    true
                }
            },
        )
        val titledDividerRecipeFactory = ViewItemRecipeFactory<TextView, String>(
            { View.inflate(context, R.layout.tableview_titled_divider, null) as TextView },
            { v, s -> v.text = s }
        )
        Rx.combineLatest(historyVM.historyColumnDatas, historyVM.activeCategories)
            .observeOn(AndroidSchedulers.mainThread())
            .distinctUntilChanged() //*idk why this emitted a copy without distinctUntilChanged
            .observe(viewLifecycleOwner) { (historyColumnDatas, activeCategories) ->
                val recipe2D =
                    listOf(
                        headerRecipeFactory.createOne2("Categories") +
                                cellRecipeFactory.createOne("Default") +
                                cellRecipeFactory.createMany(activeCategories.map { it.name }),
                        *historyColumnDatas.map {
                            columnHeaderFactory.createOne2(it) +
                                    cellRecipeFactory.createOne(it.defaultAmount.toString()) +
                                    cellRecipeFactory.createMany(activeCategories.map { k -> it.categoryAmounts[k]?.toString() ?: "" })
                        }.toTypedArray()
                    ).reflectXY()
                val dividerMap = activeCategories
                    .withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to titledDividerRecipeFactory.createOne(it.value.type.name) }
                    .mapKeys { it.key + 2 } // header row and default row
                binding.tmTableViewHistory.initialize(recipe2D, false, dividerMap, 1, 1)
            }
    }
}