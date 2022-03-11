package com.tminus1010.budgetvalue.replay_or_future.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.framework.view.viewBinding
import com.tminus1010.budgetvalue.databinding.FragCreateFuture2Binding
import com.tminus1010.budgetvalue.databinding.FragSetSearchTextsBinding
import com.tminus1010.budgetvalue.replay_or_future.presentation.CreateFuture2VM
import com.tminus1010.budgetvalue.replay_or_future.presentation.SetSearchTextsVM
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine


@AndroidEntryPoint
class SetSearchTextsFrag : Fragment(R.layout.frag_set_search_texts) {
    private val vb by viewBinding(FragSetSearchTextsBinding::bind)
    private val setSearchTextsVM by viewModels<SetSearchTextsVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # State
        vb.tmTableView.bind(setSearchTextsVM.recipeGrid) {
            initialize(
                recipeGrid = it.map { it.map { it.toViewItemRecipe(requireContext()) } },
                shouldFitItemWidthsInsideTable = true,
            )
        }
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.setSearchTextsFrag)
        }
    }
}