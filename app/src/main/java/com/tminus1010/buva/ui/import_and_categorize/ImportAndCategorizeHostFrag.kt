package com.tminus1010.buva.ui.import_and_categorize

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.buva.R
import com.tminus1010.buva.databinding.FragImportAndCategorizeHostBinding
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImportAndCategorizeHostFrag : Fragment(R.layout.frag_import_and_categorize_host) {
    lateinit var vb: FragImportAndCategorizeHostBinding
    val importAndCategorizeHostVM by viewModels<ImportAndCategorizeHostVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragImportAndCategorizeHostBinding.bind(view)
        vb.fragmentcontainerview.bind(importAndCategorizeHostVM.frag) {
            this@ImportAndCategorizeHostFrag.childFragmentManager
                .beginTransaction()
                .replace(id, it.getDeclaredConstructor().newInstance())
                .commitNowAllowingStateLoss()
        }
        vb.buttonsview.bind(importAndCategorizeHostVM.buttons) { buttons = it }
    }
}