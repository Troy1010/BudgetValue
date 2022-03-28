package com.tminus1010.budgetvalue.ui.futures

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.all_layers.extensions.onNext
import com.tminus1010.budgetvalue.all_layers.extensions.showAlertDialog
import com.tminus1010.budgetvalue.app.TransactionsInteractor
import com.tminus1010.budgetvalue.databinding.FragCreateFutureBinding
import com.tminus1010.budgetvalue.framework.androidx.viewBinding
import com.tminus1010.budgetvalue.ui.choose_categories.ChooseCategoriesFrag
import com.tminus1010.budgetvalue.ui.choose_transaction.ChooseTransactionFrag
import com.tminus1010.budgetvalue.ui.set_search_texts.SetSearchTextsFrag
import com.tminus1010.budgetvalue.ui.set_search_texts.SetSearchTextsSharedVM
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateFutureFrag : Fragment(R.layout.frag_create_future) {
    private val vb by viewBinding(FragCreateFutureBinding::bind)
    private val viewModel by viewModels<CreateFutureVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Setup
        viewModel.showAlertDialog.onNext(showAlertDialog)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { viewModel.userTryNavUp() }
        // # Events
        viewModel.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        viewModel.navToCategorySelection.observe(viewLifecycleOwner) { ChooseCategoriesFrag.navTo(nav) }
        viewModel.navToChooseTransaction.observe(viewLifecycleOwner) { ChooseTransactionFrag.navTo(nav) }
        viewModel.navToSetSearchTexts.observe(viewLifecycleOwner) { SetSearchTextsFrag.navTo(nav) }
        // # State
        vb.tmTableViewOtherInput.bind(viewModel.otherInputTableView) { it.bind(this) }
        vb.tmTableViewCategoryAmounts.bind(viewModel.categoryAmountsTableView) { it.bind(this) }
        vb.buttonsview.bind(viewModel.buttons) { buttons = it }
    }

    companion object {
        fun navTo(nav: NavController, setSearchTextsSharedVM: SetSearchTextsSharedVM, transactionsInteractor: TransactionsInteractor) {
            setSearchTextsSharedVM.searchTexts.adjustTo(listOfNotNull(transactionsInteractor.mostRecentUncategorizedSpend.value?.description))
            nav.navigate(R.id.createFutureFrag)
        }
    }
}