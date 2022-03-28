package com.tminus1010.budgetvalue._unrestructured.reconcile.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._unrestructured.reconcile.domain.ReconciliationToDo
import com.tminus1010.budgetvalue._unrestructured.reconcile.presentation.ReconciliationHostVM
import com.tminus1010.budgetvalue.databinding.FragReconciliationHostBinding
import com.tminus1010.budgetvalue.framework.androidx.viewBinding
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.view.extensions.easyToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReconciliationHostFrag : Fragment(R.layout.frag_reconciliation_host) {
    private val vb by viewBinding(FragReconciliationHostBinding::bind)
    private val reconciliationHostVM by activityViewModels<ReconciliationHostVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Presentation Event
        reconciliationHostVM.toast.observe(viewLifecycleOwner) { easyToast(it) }
        // # State
        vb.buttonsview.buttons = reconciliationHostVM.buttons
        vb.tvSubTitle.bind(reconciliationHostVM.subTitle) { text = it.toCharSequence(context) }
        vb.tvTitle.bind(reconciliationHostVM.title) { text = it.toCharSequence(context) }
        vb.frame.bind(reconciliationHostVM.currentReconciliationToDo) { (it) ->
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