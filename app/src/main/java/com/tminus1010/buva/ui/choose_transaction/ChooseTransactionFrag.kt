package com.tminus1010.buva.ui.choose_transaction

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.KEY2
import com.tminus1010.buva.all_layers.android.viewBinding
import com.tminus1010.buva.databinding.FragChooseTransactionBinding
import com.tminus1010.buva.domain.Transaction
import com.tminus1010.buva.environment.android_wrapper.ParcelableTransactionLambdaWrapper
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.easyVisibility
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChooseTransactionFrag : Fragment(R.layout.frag_choose_transaction) {
    private val vb by viewBinding(FragChooseTransactionBinding::bind)
    private val viewModel by viewModels<ChooseTransactionVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # User Intents
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { viewModel.userCancel() }
        // # State
        vb.tvNoTransactionHistory.bind(viewModel.isNoItemsMsgVisible) { easyVisibility = it }
        vb.tmTableView.bind(viewModel.tableViewVMItem) { it.bind(this) }
    }

    companion object {
        fun navTo(nav: NavController, callback: (Transaction?) -> Unit) {
            nav.navigate(
                R.id.chooseTransactionFrag,
                Bundle().apply {
                    putParcelable(KEY2, ParcelableTransactionLambdaWrapper(callback))
                }
            )
        }
    }
}
