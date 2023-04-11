package com.tminus1010.buva.ui.importZ.transactions

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.android.viewBinding
import com.tminus1010.buva.databinding.FragImportTransactionsBinding
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImportTransactionsFrag : Fragment(R.layout.frag_import_transactions) {
    private val viewModel by viewModels<ImportTransactionsVM>()
    private val vb by viewBinding(FragImportTransactionsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # State
        vb.tvMostRecentImportDate.bind(viewModel.text) { text = it.toCharSequence(requireContext()) }
        vb.buttonsview.bind(viewModel.buttons) { buttons = it }
    }
}