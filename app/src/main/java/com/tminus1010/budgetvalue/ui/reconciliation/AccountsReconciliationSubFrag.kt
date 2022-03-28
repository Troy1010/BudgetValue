package com.tminus1010.budgetvalue.ui.reconciliation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.ui.reconciliation.AccountsReconciliationVM
import com.tminus1010.budgetvalue.databinding.ItemTmTableViewBinding
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountsReconciliationSubFrag : Fragment(R.layout.item_tm_table_view) {
    private lateinit var vb: ItemTmTableViewBinding
    private val viewModel by viewModels<AccountsReconciliationVM>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = ItemTmTableViewBinding.bind(view)
        // # State
        vb.tmTableView.bind(viewModel.reconciliationTableView) { it.bind(this) }
    }
}