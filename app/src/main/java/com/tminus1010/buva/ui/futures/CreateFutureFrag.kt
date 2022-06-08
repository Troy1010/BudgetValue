package com.tminus1010.buva.ui.futures

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.data.service.MoshiProvider
import com.tminus1010.buva.databinding.FragCreateFutureBinding
import com.tminus1010.buva.framework.android.viewBinding
import com.tminus1010.buva.ui.choose_categories.ChooseCategoriesFrag
import com.tminus1010.buva.ui.choose_transaction.ChooseTransactionFrag
import com.tminus1010.buva.ui.receipt_categorization.ReceiptCategorizationHostFrag
import com.tminus1010.tmcommonkotlin.androidx.ShowAlertDialog
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class CreateFutureFrag : Fragment(R.layout.frag_create_future) {
    private val vb by viewBinding(FragCreateFutureBinding::bind)
    private val viewModel by viewModels<CreateFutureVM>()

    @Inject
    lateinit var moshiProvider: MoshiProvider
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Setup
        viewModel.showAlertDialog.onNext(ShowAlertDialog(requireActivity()))
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { viewModel.userTryNavUp() }
        // # Events
        viewModel.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        viewModel.navToCategorySelection.observe(viewLifecycleOwner) { ChooseCategoriesFrag.navTo(nav) }
        viewModel.navToChooseTransaction.observe(viewLifecycleOwner) { ChooseTransactionFrag.navTo(nav) }
        viewModel.navToReceiptCategorization.observe(viewLifecycleOwner) { ReceiptCategorizationHostFrag.navTo(nav, it, moshiProvider) }
        // # State
        vb.tmTableViewOtherInput.bind(viewModel.optionsTableView) { it.bind(this) }
        vb.tmTableViewCategoryAmounts.bind(viewModel.categoryAmountsTableView) { it.bind(this) }
        vb.buttonsview.bind(viewModel.buttons) { buttons = it }
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.createFutureFrag)
        }
    }
}