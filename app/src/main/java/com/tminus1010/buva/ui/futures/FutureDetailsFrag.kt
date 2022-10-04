package com.tminus1010.buva.ui.futures

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.databinding.FragCreateFutureBinding
import com.tminus1010.buva.domain.Future
import com.tminus1010.buva.all_layers.android.viewBinding
import com.tminus1010.buva.ui.choose_categories.ChooseCategoriesSharedVM
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class FutureDetailsFrag : Fragment(R.layout.frag_create_future) {
    private val vb by viewBinding(FragCreateFutureBinding::bind)
    private val viewModel by viewModels<FutureDetailsVM>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Setup
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { viewModel.userTryNavUp() }
        // # Events
        viewModel.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        viewModel.navToChooseCategories.observe(viewLifecycleOwner) { nav.navigate(R.id.chooseCategoriesFrag) }
        viewModel.navToChooseTransaction.observe(viewLifecycleOwner) { nav.navigate(R.id.chooseTransactionFrag) }
        viewModel.navToSetSearchTexts.observe(viewLifecycleOwner) { nav.navigate(R.id.setSearchTextsFrag) }
        // # State
        vb.tmTableViewOtherInput.bind(viewModel.optionsTableView) { it.bind(this) }
        vb.tmTableViewCategoryAmounts.bind(viewModel.categoryAmountsTableView) { it.bind(this) }
        vb.buttonsview.bind(viewModel.buttons) { buttons = it }
    }

    companion object {
        fun navTo(nav: NavController, future: Future, chooseCategoriesSharedVM: ChooseCategoriesSharedVM) {
            runBlocking { chooseCategoriesSharedVM.clearSelection(); chooseCategoriesSharedVM.selectCategories(*future.categoryAmountFormulas.keys.toTypedArray()) }
            nav.navigate(R.id.futureDetailsFrag, Bundle().apply {
                putParcelable(KEY1, future)
            })
        }
    }
}