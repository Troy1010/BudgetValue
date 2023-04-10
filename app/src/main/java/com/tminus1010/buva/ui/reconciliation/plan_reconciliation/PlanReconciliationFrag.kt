package com.tminus1010.buva.ui.reconciliation.plan_reconciliation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.databinding.FragPlanReconciliationBinding
import com.tminus1010.buva.domain.ReconciliationToDo
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlanReconciliationFrag : Fragment(R.layout.frag_plan_reconciliation) {
    lateinit var vb: FragPlanReconciliationBinding
    val viewModel by viewModels<PlanReconciliationVM>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragPlanReconciliationBinding.bind(view)
        // # State
        vb.tmTableView.bind(viewModel.reconciliationTableView) { it.bind(this) }
        vb.tvSubtitle.bind(viewModel.subTitle) { text = it }
    }

    companion object {
        fun create(reconciliationToDo: ReconciliationToDo.PlanZ): PlanReconciliationFrag {
            return PlanReconciliationFrag().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY1, reconciliationToDo)
                }
            }
        }
    }
}