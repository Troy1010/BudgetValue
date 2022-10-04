package com.tminus1010.buva.ui.choose_transaction

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.buva.R
import com.tminus1010.buva.databinding.FragChooseTransactionBinding
import com.tminus1010.buva.all_layers.android.viewBinding
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.easyVisibility
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChooseTransactionFrag : Fragment(R.layout.frag_choose_transaction) {
    private val vb by viewBinding(FragChooseTransactionBinding::bind)
    private val viewModel by viewModels<ChooseTransactionVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Events
        viewModel.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        // # State
        vb.tvNoTransactionHistory.bind(viewModel.isNoItemsMsgVisible) { easyVisibility = it }
        vb.tmTableView.bind(viewModel.recipeGrid) {
            initialize(
                recipeGrid = it.map { it.map { it.toViewItemRecipe(requireContext()) } },
                shouldFitItemWidthsInsideTable = true,
            )
        }
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.chooseTransactionFrag)
        }
    }
}
