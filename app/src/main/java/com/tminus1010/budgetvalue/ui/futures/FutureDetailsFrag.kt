package com.tminus1010.budgetvalue.ui.futures

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.all_layers.KEY1
import com.tminus1010.budgetvalue.all_layers.extensions.easyEmit
import com.tminus1010.budgetvalue.data.service.MoshiWithCategoriesProvider
import com.tminus1010.budgetvalue.databinding.FragCreateFutureBinding
import com.tminus1010.budgetvalue.domain.Future
import com.tminus1010.budgetvalue.domain.TransactionMatcher
import com.tminus1010.budgetvalue.framework.android.viewBinding
import com.tminus1010.budgetvalue.ui.choose_categories.ChooseCategoriesSharedVM
import com.tminus1010.budgetvalue.ui.set_search_texts.SetSearchTextsSharedVM
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class FutureDetailsFrag : Fragment(R.layout.frag_create_future) {
    private val vb by viewBinding(FragCreateFutureBinding::bind)
    private val viewModel by viewModels<FutureDetailsVM>()

    @Inject
    lateinit var moshiWithCategoriesProvider: MoshiWithCategoriesProvider

    val future: Future
        get() = moshiWithCategoriesProvider.moshi.fromJson<Future>(requireArguments().getString(KEY1)) ?: error("Oh no!") // TODO: Could use a SharedVM instead..?

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Setup
        viewModel.future.easyEmit(future)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { viewModel.userTryNavUp() }
        // # Events
        viewModel.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        viewModel.navToCategorySelection.observe(viewLifecycleOwner) { nav.navigate(R.id.chooseCategoriesFrag) }
        viewModel.navToChooseTransaction.observe(viewLifecycleOwner) { nav.navigate(R.id.chooseTransactionFrag) }
        viewModel.navToSetSearchTexts.observe(viewLifecycleOwner) { nav.navigate(R.id.setSearchTextsFrag) }
        // # State
        vb.tmTableViewOtherInput.bind(viewModel.otherInputTableView) { it.bind(this) }
        vb.tmTableViewCategoryAmounts.bind(viewModel.categoryAmountsTableView) { it.bind(this) }
        vb.buttonsview.bind(viewModel.buttons) { buttons = it }
    }

    companion object {
        fun navTo(nav: NavController, moshiWithCategoriesProvider: MoshiWithCategoriesProvider, future: Future, chooseCategoriesSharedVM: ChooseCategoriesSharedVM, setSearchTextsSharedVM: SetSearchTextsSharedVM) {
            setSearchTextsSharedVM.searchTexts.adjustTo((future.onImportMatcher as? TransactionMatcher.Multi)?.transactionMatchers?.filterIsInstance<TransactionMatcher.SearchText>()?.map { it.searchText } ?: listOfNotNull((future.onImportMatcher as? TransactionMatcher.SearchText)?.searchText))
            runBlocking { chooseCategoriesSharedVM.clearSelection(); chooseCategoriesSharedVM.selectCategories(*future.categoryAmountFormulas.keys.toTypedArray()) }
            nav.navigate(R.id.futureDetailsFrag, Bundle().apply {
                putString(KEY1, moshiWithCategoriesProvider.moshi.toJson(future))
            })
        }
    }
}