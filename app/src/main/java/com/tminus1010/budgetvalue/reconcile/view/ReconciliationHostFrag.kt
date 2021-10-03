package com.tminus1010.budgetvalue.reconcile.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.extensions.bind
import com.tminus1010.budgetvalue._core.middleware.view.viewBinding
import com.tminus1010.budgetvalue.all.domain.models.ReconciliationToDo
import com.tminus1010.budgetvalue.databinding.FragReconciliationHostBinding
import com.tminus1010.budgetvalue.reconcile.presentation.ReconciliationHostVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReconciliationHostFrag : Fragment(R.layout.frag_reconciliation_host) {
    private val vb by viewBinding(FragReconciliationHostBinding::bind)
    private val reconciliationHostVM by activityViewModels<ReconciliationHostVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Presentation State
        vb.buttonsview.buttons = reconciliationHostVM.buttons
        vb.tvSubTitle.bind(reconciliationHostVM.subTitle) { text = it.getString(context) }
        vb.tvTitle.bind(reconciliationHostVM.title) { text = it.getString(context) }
        vb.frame.bind(reconciliationHostVM.currentReconciliationToDo) { (it) ->
            this@ReconciliationHostFrag.childFragmentManager
                .beginTransaction()
                .replace(
                    R.id.frame,
                    when (it) {
                        is ReconciliationToDo.Accounts -> AccountsReconciliationSubFrag()
                        is ReconciliationToDo.PlanZ -> PlanReconciliationSubFrag(it)
                        null,
                        is ReconciliationToDo.Anytime -> AnytimeReconciliationSubFrag()
                    },
                    null,
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