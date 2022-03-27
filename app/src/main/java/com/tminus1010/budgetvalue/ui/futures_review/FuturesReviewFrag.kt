package com.tminus1010.budgetvalue.ui.futures_review

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.app.TransactionsInteractor
import com.tminus1010.budgetvalue.data.service.MoshiWithCategoriesProvider
import com.tminus1010.budgetvalue.databinding.FragFuturesReviewBinding
import com.tminus1010.budgetvalue.framework.view.viewBinding
import com.tminus1010.budgetvalue.ui.create_future.CreateFutureFrag
import com.tminus1010.budgetvalue.ui.create_future.ReplayOrFutureDetailsFrag
import com.tminus1010.budgetvalue.ui.select_categories.SelectCategoriesModel
import com.tminus1010.budgetvalue.ui.set_search_texts.SetSearchTextsSharedVM
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.easyVisibility
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FuturesReviewFrag : Fragment(R.layout.frag_futures_review) {
    private val vb by viewBinding(FragFuturesReviewBinding::bind)
    private val viewModel by viewModels<FuturesReviewVM>()

    @Inject
    lateinit var moshiWithCategoriesProvider: MoshiWithCategoriesProvider

    @Inject
    lateinit var selectCategoriesModel: SelectCategoriesModel

    @Inject
    lateinit var setSearchTextsSharedVM: SetSearchTextsSharedVM

    @Inject
    lateinit var transactionsInteractor: TransactionsInteractor

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Events
        viewModel.navToFutureDetails.observe(viewLifecycleOwner) { ReplayOrFutureDetailsFrag.navTo(nav, moshiWithCategoriesProvider, it, selectCategoriesModel, setSearchTextsSharedVM) }
        viewModel.navToCreateFuture.observe(viewLifecycleOwner) { CreateFutureFrag.navTo(nav, setSearchTextsSharedVM, transactionsInteractor) }
        // # State
        vb.tvNoFutures.bind(viewModel.isNoFutureTextVisible) { easyVisibility = it }
        vb.buttonsview.bind(viewModel.buttons) { buttons = it }
        vb.tmTableViewFutures.bind(viewModel.isNoFutureTextVisible) { easyVisibility = !it }
        vb.tmTableViewFutures.bind(viewModel.futuresRecipeGrid) {
            initialize(
                recipeGrid = it.map { it.map { it.toViewItemRecipe(requireContext()) } },
                shouldFitItemWidthsInsideTable = true,
                rowFreezeCount = 1,
            )
        }
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.futuresReviewFrag)
        }
    }
}