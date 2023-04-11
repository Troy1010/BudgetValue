package com.tminus1010.buva.ui.budget.reconciliation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.android.viewBinding
import com.tminus1010.buva.databinding.FragReconciliationHostBinding
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReconciliationHostFrag : Fragment(R.layout.frag_reconciliation_host) {
    private val vb by viewBinding(FragReconciliationHostBinding::bind)
    private val viewModel by activityViewModels<ReconciliationHostVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # State
        vb.buttonsview.bind(viewModel.buttons) { buttons = it }
        vb.tvSubTitle.bind(viewModel.subTitle) { text = it.toCharSequence(context) }
        vb.tvTitle.bind(viewModel.title) { text = it.toCharSequence(context) }
        vb.frame.bind(viewModel.fragFactory) { fragFactory ->
            this@ReconciliationHostFrag.childFragmentManager
                .beginTransaction()
                .replace(id, fragFactory())
                .commitNowAllowingStateLoss()
        }
    }
}