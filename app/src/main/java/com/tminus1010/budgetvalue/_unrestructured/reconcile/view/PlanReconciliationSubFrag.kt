package com.tminus1010.budgetvalue._unrestructured.reconcile.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._unrestructured.reconcile.domain.ReconciliationToDo
import com.tminus1010.budgetvalue._unrestructured.reconcile.presentation.PlanReconciliationVM
import com.tminus1010.budgetvalue.databinding.ItemTmTableViewBinding
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlanReconciliationSubFrag : Fragment(R.layout.item_tm_table_view) {
    lateinit var vb: ItemTmTableViewBinding
    val viewModel by viewModels<PlanReconciliationVM>()
    val reconciliationToDo = PlanReconciliationSubFrag.reconciliationToDo ?: error("reconciliationToDo was null, restart required.")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = ItemTmTableViewBinding.bind(view)
        // # Setup
        viewModel.reconciliationToDo.onNext(reconciliationToDo)
        // # State
        vb.tmTableView.bind(viewModel.reconciliationTableView) { it.bind(this) }
    }

    companion object {
        private var reconciliationToDo: ReconciliationToDo.PlanZ? = null
        operator fun invoke(reconciliationToDo: ReconciliationToDo.PlanZ): PlanReconciliationSubFrag {
            this.reconciliationToDo = reconciliationToDo
            return PlanReconciliationSubFrag()
        }
    }
}