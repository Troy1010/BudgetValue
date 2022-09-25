package com.tminus1010.buva.ui.reconciliation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.databinding.SubfragPlanReconciliationBinding
import com.tminus1010.buva.domain.ReconciliationToDo
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlanReconciliationSubFrag : Fragment(R.layout.subfrag_plan_reconciliation) {
    lateinit var vb: SubfragPlanReconciliationBinding
    val viewModel by viewModels<PlanReconciliationVM>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = SubfragPlanReconciliationBinding.bind(view)
        // # State
        vb.tmTableView.bind(viewModel.reconciliationTableView) { it.bind(this) }
        vb.tvSubtitle.bind(viewModel.subTitle) { text = it }
    }

    companion object {
        operator fun invoke(reconciliationToDo: ReconciliationToDo.PlanZ): PlanReconciliationSubFrag {
            return PlanReconciliationSubFrag().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY1, reconciliationToDo)
                }
            }
        }
    }
}