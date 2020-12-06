package com.example.budgetvalue.layer_ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.budgetvalue.App
import com.example.budgetvalue.R
import com.tminus1010.tmcommonkotlin.logz.logz
import com.tminus1010.tmcommonkotlin.misc.createVmFactory
import com.tminus1010.tmcommonkotlin_rx.observe

class HistoryFrag: Fragment(R.layout.frag_history) {
    val app by lazy { requireActivity().application as App }
    val repo by lazy { app.appComponent.getRepo() }
    val transactionsVM: TransactionsVM by activityViewModels { createVmFactory { TransactionsVM(repo) } }
    val accountsVM: AccountsVM by activityViewModels{ createVmFactory { AccountsVM(repo) }}
    val categoriesAppVM by lazy { app.appComponent.getCategoriesAppVM() }
    val planVM: PlanVM by activityViewModels{ createVmFactory { PlanVM(repo, categoriesAppVM) }}
    val reconcileVM: ReconcileVM by activityViewModels { createVmFactory { ReconcileVM(repo, transactionsVM.spends, accountsVM.accountsTotal, planVM) } }
    val datePeriodGetter by lazy { app.appComponent.getDatePeriodGetter() }
    val historyVM: HistoryVM by activityViewModels { createVmFactory { HistoryVM(transactionsVM, reconcileVM, planVM, datePeriodGetter) } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        historyVM.stateHistoryColumnData
            .observe(viewLifecycleOwner) {

            }
    }
}