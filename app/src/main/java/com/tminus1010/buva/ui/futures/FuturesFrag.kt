package com.tminus1010.buva.ui.futures

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.buva.R
import com.tminus1010.buva.app.TransactionsInteractor
import com.tminus1010.buva.data.service.MoshiWithCategoriesProvider
import com.tminus1010.buva.databinding.FragFuturesBinding
import com.tminus1010.buva.framework.android.viewBinding
import com.tminus1010.buva.ui.choose_categories.ChooseCategoriesSharedVM
import com.tminus1010.buva.ui.set_search_texts.SetSearchTextsSharedVM
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.easyVisibility
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FuturesFrag : Fragment(R.layout.frag_futures) {
    private val vb by viewBinding(FragFuturesBinding::bind)
    private val viewModel by viewModels<FuturesVM>()

    @Inject
    lateinit var moshiWithCategoriesProvider: MoshiWithCategoriesProvider

    @Inject
    lateinit var chooseCategoriesSharedVM: ChooseCategoriesSharedVM

    @Inject
    lateinit var setSearchTextsSharedVM: SetSearchTextsSharedVM

    @Inject
    lateinit var transactionsInteractor: TransactionsInteractor

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Events
        viewModel.navToFutureDetails.observe(viewLifecycleOwner) { FutureDetailsFrag.navTo(nav, moshiWithCategoriesProvider, it, chooseCategoriesSharedVM, setSearchTextsSharedVM) }
        viewModel.navToCreateFuture.observe(viewLifecycleOwner) { CreateFutureFrag.navTo(nav, setSearchTextsSharedVM, transactionsInteractor) }
        // # State
        vb.tvNoFutures.bind(viewModel.isNoFutureTextVisible) { easyVisibility = it }
        vb.tmTableViewFutures.bind(viewModel.isNoFutureTextVisible) { easyVisibility = !it }
        vb.tmTableViewFutures.bind(viewModel.futuresTableView) { it.bind(this) }
        vb.buttonsview.bind(viewModel.buttons) { buttons = it }
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.futuresFrag)
        }
    }
}