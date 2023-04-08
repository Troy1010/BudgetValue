package com.tminus1010.buva.ui.futures

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.buva.R
import com.tminus1010.buva.databinding.FragFuturesBinding
import com.tminus1010.buva.all_layers.android.viewBinding
import com.tminus1010.buva.ui.choose_categories.ChooseCategoriesSharedVM
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.easyVisibility
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FuturesFrag : Fragment(R.layout.frag_futures) {
    private val vb by viewBinding(FragFuturesBinding::bind)
    private val viewModel by viewModels<FuturesVM>()

    @Inject
    lateinit var chooseCategoriesSharedVM: ChooseCategoriesSharedVM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Events
        viewModel.navToFutureDetails.observe(viewLifecycleOwner) { FutureDetailsFrag.navTo(nav, it, chooseCategoriesSharedVM) }
        viewModel.navToCreateFuture.observe(viewLifecycleOwner) { CreateFutureFrag.navTo(nav) }
        // # State
        vb.tvNoFutures.bind(viewModel.isNoFutureTextVisible) { easyVisibility = it }
        vb.tmTableViewFutures.bind(viewModel.isNoFutureTextVisible) { easyVisibility = !it }
        vb.tmTableViewFutures.bind(viewModel.futuresTableView) { it.bind(this) }
        vb.buttonsview.bind(viewModel.buttons) { buttons = it }
    }
}