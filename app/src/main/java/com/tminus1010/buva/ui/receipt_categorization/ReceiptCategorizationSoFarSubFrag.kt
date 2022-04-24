package com.tminus1010.buva.ui.receipt_categorization

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.buva.R
import com.tminus1010.buva.databinding.SubfragReceiptCategorizationSoFarBinding
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReceiptCategorizationSoFarSubFrag : Fragment(R.layout.subfrag_receipt_categorization_so_far) {
    lateinit var vb: SubfragReceiptCategorizationSoFarBinding
    private val viewModel by viewModels<ReceiptCategorizationSoFarVM>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = SubfragReceiptCategorizationSoFarBinding.bind(view)
        // # State
        vb.tmTableView.bind(viewModel.categoryAmountsTableView) { it.bind(this) }
    }
}