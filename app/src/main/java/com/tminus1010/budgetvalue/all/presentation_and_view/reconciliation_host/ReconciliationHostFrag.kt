package com.tminus1010.budgetvalue.all.presentation_and_view.reconciliation_host

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.extensions.bind
import com.tminus1010.budgetvalue._core.middleware.view.viewBinding
import com.tminus1010.budgetvalue.databinding.FragReconciliationHostBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReconciliationHostFrag : Fragment(R.layout.frag_reconciliation_host) {
    private val vb by viewBinding(FragReconciliationHostBinding::bind)
    private val reconciliationHostVM by activityViewModels<ReconciliationHostVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Presentation State
        vb.buttonsview.buttons = reconciliationHostVM.buttons
        vb.tvTitle.bind(reconciliationHostVM.title) { text = it.getString(context) }
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.reconciliationHostFrag)
        }
    }
}