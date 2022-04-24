package com.tminus1010.buva.ui.reconciliation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.buva.R
import com.tminus1010.buva.domain.ReconciliationToDo
import com.tminus1010.buva.databinding.ItemTmTableViewBinding
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlanReconciliationSubFrag : Fragment(R.layout.item_tm_table_view) {
    lateinit var vb: ItemTmTableViewBinding
    val viewModel by viewModels<PlanReconciliationVM>()
    val reconciliationToDo = Companion.reconciliationToDo ?: error("reconciliationToDo was null, restart required.")
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
            Companion.reconciliationToDo = reconciliationToDo
            return PlanReconciliationSubFrag()
        }
    }
}