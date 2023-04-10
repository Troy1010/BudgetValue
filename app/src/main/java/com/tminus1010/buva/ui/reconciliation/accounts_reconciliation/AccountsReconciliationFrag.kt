package com.tminus1010.buva.ui.reconciliation.accounts_reconciliation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.buva.R
import com.tminus1010.buva.databinding.ItemTmTableViewBinding
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountsReconciliationFrag : Fragment(R.layout.item_tm_table_view) {
    private lateinit var vb: ItemTmTableViewBinding
    private val viewModel by viewModels<AccountsReconciliationVM>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = ItemTmTableViewBinding.bind(view)
        // # State
        vb.tmTableView.bind(viewModel.reconciliationTableView) { it.bind(this) }
    }
}