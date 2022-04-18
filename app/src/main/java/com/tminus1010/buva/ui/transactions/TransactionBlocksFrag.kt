package com.tminus1010.buva.ui.transactions

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.buva.R
import com.tminus1010.buva.databinding.FragTransactionBlocksBinding
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.misc.extensions.lifecycleOwner
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionBlocksFrag : Fragment(R.layout.frag_transaction_blocks) {
    val transactionBlockCompletionVM by viewModels<TransactionBlocksVM>()
    lateinit var vb: FragTransactionBlocksBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragTransactionBlocksBinding.bind(view)
        vb.root.lifecycleOwner = viewLifecycleOwner
        // # State
        vb.tmTableView.bind(transactionBlockCompletionVM.transactionBlocksTableView) { it.bind(this) }
        vb.textviewTitle.bind(transactionBlockCompletionVM.title) { text = it }
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.transactionBlocksFrag)
        }
    }
}