package com.tminus1010.budgetvalue.ui.create_future

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.ui.select_categories.SelectCategoriesModel
import com.tminus1010.budgetvalue.all_layers.KEY1
import com.tminus1010.budgetvalue.all_layers.extensions.easyEmit
import com.tminus1010.budgetvalue.data.service.MoshiWithCategoriesProvider
import com.tminus1010.budgetvalue.databinding.FragCreateFutureBinding
import com.tminus1010.budgetvalue.domain.Future
import com.tminus1010.budgetvalue.domain.TransactionMatcher
import com.tminus1010.budgetvalue.framework.view.viewBinding
import com.tminus1010.budgetvalue.ui.set_search_texts.SetSearchTextsSharedVM
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class ReplayOrFutureDetailsFrag : Fragment(R.layout.frag_create_future) {
    private val vb by viewBinding(FragCreateFutureBinding::bind)
    private val viewModel by viewModels<ReplayOrFutureDetailsVM>()

    @Inject
    lateinit var moshiWithCategoriesProvider: MoshiWithCategoriesProvider

    val future: Future
        get() = moshiWithCategoriesProvider.moshi.fromJson<Future>(requireArguments().getString(KEY1)) ?: error("Oh no!")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Setup
        viewModel.future.easyEmit(future)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { viewModel.userTryNavUp() }
        // # Events
        viewModel.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        viewModel.navToCategorySelection.observe(viewLifecycleOwner) { nav.navigate(R.id.selectCategoriesFrag) }
        viewModel.navToChooseTransaction.observe(viewLifecycleOwner) { nav.navigate(R.id.chooseTransactionFrag) }
        viewModel.navToSetSearchTexts.observe(viewLifecycleOwner) { nav.navigate(R.id.setSearchTextsFrag) }
        // # State
        vb.tmTableViewOtherInput.bind(viewModel.otherInput) {
            initialize(
                recipeGrid = it.map { it.map { it.toViewItemRecipe(requireContext()) } },
                shouldFitItemWidthsInsideTable = true,
            )
        }
        vb.tmTableViewCategoryAmounts.bind(combine(viewModel.recipeGrid, viewModel.dividerMap) { a, b -> Pair(a, b) }) { (recipeGrid, dividerMap) ->
            initialize(
                recipeGrid = recipeGrid.map { it.map { it.toViewItemRecipe(requireContext()) } },
                dividerMap = dividerMap.mapValues { it.value.toViewItemRecipe(requireContext()) },
                shouldFitItemWidthsInsideTable = true,
            )
        }
        vb.buttonsview.bind(viewModel.buttons) { buttons = it }
    }

    companion object {
        fun navTo(nav: NavController, moshiWithCategoriesProvider: MoshiWithCategoriesProvider, future: Future, selectCategoriesModel: SelectCategoriesModel, setSearchTextsSharedVM: SetSearchTextsSharedVM) {
            setSearchTextsSharedVM.searchTexts.adjustTo((future.onImportMatcher as? TransactionMatcher.Multi)?.transactionMatchers?.filterIsInstance<TransactionMatcher.SearchText>()?.map { it.searchText } ?: listOfNotNull((future.onImportMatcher as? TransactionMatcher.SearchText)?.searchText))
            runBlocking { selectCategoriesModel.clearSelection(); selectCategoriesModel.selectCategories(*future.categoryAmountFormulas.keys.toTypedArray()) }
            nav.navigate(R.id.replayOrFutureDetailsFrag, Bundle().apply {
                putString(KEY1, moshiWithCategoriesProvider.moshi.toJson(future))
            })
        }
    }
}