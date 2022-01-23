package com.tminus1010.budgetvalue.transactions.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.databinding.FragTransactionBlockCompletionBinding
import com.tminus1010.budgetvalue.transactions.presentation.TransactionBlockCompletionVM
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.misc.extensions.lifecycleOwner
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionBlockCompletionFrag : Fragment(R.layout.frag_transaction_block_completion) {
    val transactionBlockCompletionVM by viewModels<TransactionBlockCompletionVM>()
    lateinit var vb: FragTransactionBlockCompletionBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragTransactionBlockCompletionBinding.bind(view)
        vb.root.lifecycleOwner = viewLifecycleOwner
        // # Presentation State
        vb.tmTableView.bind(transactionBlockCompletionVM.transactionVMItems) {
            initialize(
                recipeGrid = it.map { it.map { it.toViewItemRecipe(context) } },
                shouldFitItemWidthsInsideTable = true,
            )
        }
        vb.textviewTitle.bind(transactionBlockCompletionVM.title) { text = it }
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.transactionBlockCompletionFrag)
        }
    }
}