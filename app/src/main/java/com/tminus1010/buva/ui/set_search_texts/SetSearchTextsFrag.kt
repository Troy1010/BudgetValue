package com.tminus1010.buva.ui.set_search_texts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.buva.R
import com.tminus1010.buva.framework.android.viewBinding
import com.tminus1010.buva.databinding.FragSetSearchTextsBinding
import com.tminus1010.buva.ui.choose_transaction.ChooseTransactionFrag
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SetSearchTextsFrag : Fragment(R.layout.frag_set_search_texts) {
    private val vb by viewBinding(FragSetSearchTextsBinding::bind)
    private val viewModel by viewModels<SetSearchTextsVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Events
        viewModel.navToChooseTransaction.observe(viewLifecycleOwner) { ChooseTransactionFrag.navTo(nav) }
        // # State
        vb.tmTableView.bind(viewModel.recipeGrid) {
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