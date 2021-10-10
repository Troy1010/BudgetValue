package com.tminus1010.budgetvalue.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.all.extensions.bind
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
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.schedulers.Schedulers

@AndroidEntryPoint
class HistoryFrag : Fragment(R.layout.frag_history) {
    private val historyVM by activityViewModels<HistoryVM>()
    private val vb by viewBinding(FragHistoryBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Presentation Events
        historyVM.showPopupMenu.observe(viewLifecycleOwner) { (view, menuItems) -> PopupMenu(requireActivity(), view).show(menuItems) }
        // # Presentation State
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