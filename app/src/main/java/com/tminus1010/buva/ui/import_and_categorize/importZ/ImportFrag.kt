package com.tminus1010.buva.ui.import_and_categorize.importZ

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.android.viewBinding
import com.tminus1010.buva.databinding.FragImportBinding
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImportFrag : Fragment(R.layout.frag_import) {
    private val viewModel by viewModels<ImportVM>()
    private val vb by viewBinding(FragImportBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # State
        vb.tv.bind(viewModel.text) { text = it.toCharSequence(requireContext()) }
        vb.buttonsview.bind(viewModel.buttons) { buttons = it }
    }
}