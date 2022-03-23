package com.tminus1010.budgetvalue.transactions.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.all_features.framework.view.viewBinding
import com.tminus1010.budgetvalue.databinding.FragChooseTransactionBinding
import com.tminus1010.budgetvalue.transactions.presentation.ChooseTransactionSharedVM
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ChooseTransactionFrag : Fragment(R.layout.frag_choose_transaction) {
    private val vb by viewBinding(FragChooseTransactionBinding::bind)

    @Inject
    lateinit var chooseTransactionSharedVM: ChooseTransactionSharedVM
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Events
        chooseTransactionSharedVM.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        // # State
        vb.tvNoTransactionHistory.bind(chooseTransactionSharedVM.isNoItemsMsgVisible) { visibility = if (it) View.VISIBLE else View.GONE }
        vb.tmTableView.bind(chooseTransactionSharedVM.recipeGrid) {
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
