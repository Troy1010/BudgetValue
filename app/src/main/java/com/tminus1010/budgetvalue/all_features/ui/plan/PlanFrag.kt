package com.tminus1010.budgetvalue.all_features.ui.plan

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.all_features.framework.view.viewBinding
import com.tminus1010.budgetvalue.databinding.FragPlanBinding
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine

@AndroidEntryPoint
class PlanFrag : Fragment(R.layout.frag_plan) {
    private val vb by viewBinding(FragPlanBinding::bind)
    private val viewModel: PlanVM by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # State
        vb.tmTableView.bind(combine(viewModel.recipeGrid, viewModel.dividerMap) { a, b -> Pair(a, b) }) { (recipeGrid, dividerMap) ->
            initialize(
                recipeGrid = recipeGrid.map { it.map { it.toViewItemRecipe(requireContext()) } },
                dividerMap = dividerMap.mapValues { it.value.toViewItemRecipe(requireContext()) },
                shouldFitItemWidthsInsideTable = true,
                rowFreezeCount = 1,
            )
        }
        vb.buttonsview.bind(viewModel.buttons) { buttons = it }
    }
}