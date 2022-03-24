package com.tminus1010.budgetvalue._unrestructured.replay_or_future.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.databinding.FragReplaysBinding
import com.tminus1010.budgetvalue._unrestructured.replay_or_future.presentation.UseReplayVM
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UseReplayFrag : Fragment(R.layout.frag_replays) {
    private lateinit var vb: FragReplaysBinding
    val useReplayVM by viewModels<UseReplayVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragReplaysBinding.bind(view)
        // # Events
        useReplayVM.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        // # State
        vb.tmTableView.bind(useReplayVM.replays) {
            initialize(
                recipeGrid = it.map { listOf(it.toViewItemRecipe(requireContext())) },
                shouldFitItemWidthsInsideTable = true,
            )
        }
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.futuresReviewFrag)
        }
    }
}