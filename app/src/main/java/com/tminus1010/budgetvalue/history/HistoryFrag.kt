package com.tminus1010.budgetvalue.history

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.all.extensions.show
import com.tminus1010.budgetvalue._core.framework.view.recipe_factories.itemTitledDividerRB
import com.tminus1010.budgetvalue._core.framework.view.viewBinding
import com.tminus1010.budgetvalue.databinding.FragHistoryBinding
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.kotlin.Observables

@AndroidEntryPoint
class HistoryFrag : Fragment(R.layout.frag_history) {
    private val historyVM by activityViewModels<HistoryVM>()
    private val vb by viewBinding(FragHistoryBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Presentation Events
        historyVM.showPopupMenu.observe(viewLifecycleOwner) { (view, menuItems) -> PopupMenu(requireActivity(), view).show(menuItems) }
        // # State
        vb.tmTableViewHistory.bind(Observables.combineLatest(historyVM.recipeGrid, historyVM.dividerMap)) { (recipeGrid, dividerMap) ->
            initialize(
                recipeGrid.map { it.map { it.toViewItemRecipe(context) } },
                shouldFitItemWidthsInsideTable = false,
                dividerMap = dividerMap.mapValues { itemTitledDividerRB().create(it.value) },
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