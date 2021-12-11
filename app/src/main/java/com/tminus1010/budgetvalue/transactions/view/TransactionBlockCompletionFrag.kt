package com.tminus1010.budgetvalue.transactions.view

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R

class TransactionBlockCompletionFrag : Fragment(R.layout.frag_transaction_block_completion) {
    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.transactionBlockCompletionFrag)
        }
    }
}