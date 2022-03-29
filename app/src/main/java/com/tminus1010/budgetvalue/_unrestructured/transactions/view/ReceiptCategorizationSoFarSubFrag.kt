package com.tminus1010.budgetvalue._unrestructured.transactions.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._unrestructured.transactions.presentation.ReceiptCategorizationSoFarVM
import com.tminus1010.budgetvalue.databinding.SubfragReceiptCategorizationSoFarBinding
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReceiptCategorizationSoFarSubFrag : Fragment(R.layout.subfrag_receipt_categorization_so_far) {
    lateinit var vb: SubfragReceiptCategorizationSoFarBinding
    private val receiptCategorizationSoFarVM by viewModels<ReceiptCategorizationSoFarVM>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = SubfragReceiptCategorizationSoFarBinding.bind(view)
        // # State
        vb.tmTableView.bind(receiptCategorizationSoFarVM.categoryAmountsTableView) { it.bind(this) }
    }
}