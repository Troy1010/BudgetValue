package com.tminus1010.budgetvalue.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.all.extensions.show
import com.tminus1010.budgetvalue._core.middleware.view.recipe_factories.itemTextViewRB
import com.tminus1010.budgetvalue._core.middleware.view.recipe_factories.itemTitledDividerRB
import com.tminus1010.budgetvalue._core.middleware.view.tmTableView3.ViewItemRecipeFactory3
import com.tminus1010.budgetvalue._core.middleware.view.viewBinding
import com.tminus1010.budgetvalue.databinding.FragHistoryBinding
import com.tminus1010.budgetvalue.databinding.ItemHeaderWithSubtitleBinding
import com.tminus1010.tmcommonkotlin.core.extensions.reflectXY
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.schedulers.Schedulers

@AndroidEntryPoint
class HistoryFrag : Fragment(R.layout.frag_history) {
    private val historyVM: HistoryVM by activityViewModels()
    private val vb by viewBinding(FragHistoryBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # TMTableView
        val columnHeaderFactory = ViewItemRecipeFactory3(
            { ItemHeaderWithSubtitleBinding.inflate(LayoutInflater.from(requireContext())) },
            { d: HistoryVMItem, vb, _ ->
                vb.textviewHeader.text = d.title
                vb.textviewSubtitle.text = d.subTitle.value!!.first // TODO("Duct-tape solution to non-resizing frozen row")
                vb.root.setOnLongClickListener {
                    PopupMenu(requireActivity(), vb.root).show(d.menuVMItems)
                    true
                }
            },
        )
        Observables.combineLatest(historyVM.historyVMItems, historyVM.activeCategories)
            .distinctUntilChanged() //*idk why this emitted a copy without distinctUntilChanged
            .observeOn(Schedulers.computation())
            .map { (historyVMItems, activeCategories) ->
                val recipe2D =
                    listOf(
                        listOf(
                            itemTextViewRB().create("Categories"),
                            itemTextViewRB().create("Default"),
                            *activeCategories.map {
                                itemTextViewRB().create(it.name)
                            }.toTypedArray()
                        ),
                        *historyVMItems.map { historyVMItem ->
                            listOf(
                                columnHeaderFactory.createOne(historyVMItem),
                                itemTextViewRB().create(historyVMItem.defaultAmount),
                                *historyVMItem.amountStrings(activeCategories).map {
                                    itemTextViewRB().create(it)
                                }.toTypedArray()
                            )
                        }.toTypedArray()
                    ).reflectXY()
                val dividerMap = activeCategories
                    .withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to itemTitledDividerRB().create(it.value.name) }
                    .mapKeys { it.key + 2 } // header row and default row
                Pair(recipe2D, dividerMap)
            }
            .observe(viewLifecycleOwner) { (recipe2D, dividerMap) ->
                vb.tmTableViewHistory.initialize(
                    recipeGrid = recipe2D,
                    shouldFitItemWidthsInsideTable = false,
                    dividerMap = dividerMap,
                    colFreezeCount = 1,
                    rowFreezeCount = 1,
                )
            }
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.historyFrag)
        }
    }
}