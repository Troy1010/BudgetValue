package com.tminus1010.buva.ui.reconciliation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import com.tminus1010.buva.R
import com.tminus1010.buva.domain.ReconciliationToDo
import com.tminus1010.buva.databinding.FragReconciliationHostBinding
import com.tminus1010.buva.framework.android.viewBinding
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReconciliationHostFrag : Fragment(R.layout.frag_reconciliation_host) {
    private val vb by viewBinding(FragReconciliationHostBinding::bind)
    private val reconciliationHostVM by activityViewModels<ReconciliationHostVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # State
        vb.buttonsview.buttons = reconciliationHostVM.buttons
        vb.tvSubTitle.bind(reconciliationHostVM.subTitle) { text = it.toCharSequence(context) }
        vb.tvTitle.bind(reconciliationHostVM.title) { text = it.toCharSequence(context) }
        vb.frame.bind(reconciliationHostVM.currentReconciliationToDo) {
            this@ReconciliationHostFrag.childFragmentManager
                .beginTransaction()
                .replace(
                    R.id.frame,
                    when (it) {
                        is ReconciliationToDo.PlanZ -> PlanReconciliationSubFrag(it)
                        is ReconciliationToDo.Accounts,
                        is ReconciliationToDo.Anytime,
                        null,
                        -> AccountsReconciliationSubFrag()
                    },
                )
                .commitNowAllowingStateLoss()
        }
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.reconciliationHostFrag)
        }
    }
}