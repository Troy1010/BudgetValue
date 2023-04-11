package com.tminus1010.buva.ui.import_and_categorize

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.databinding.FragImportAndCategorizeHostBinding
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImportAndCategorizeHostFrag : Fragment(R.layout.frag_import_and_categorize_host) {
    lateinit var vb: FragImportAndCategorizeHostBinding
    val viewModel by viewModels<ImportAndCategorizeHostVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragImportAndCategorizeHostBinding.bind(view)
        vb.fragmentcontainerview.bind(viewModel.fragFactory) { fragFactory ->
            this@ImportAndCategorizeHostFrag.childFragmentManager
                .beginTransaction()
                .replace(id, fragFactory())
                .commitNowAllowingStateLoss()
        }
        vb.bottomnavigationview.setOnItemSelectedListener { viewModel.userSelectMenuItem(it.itemId); true }
    }

    companion object {
        fun navTo(nav: NavController, subNavId: Int? = null) {
            nav.navigate(
                R.id.importAndCategorizeHostFrag,
                Bundle().apply {
                    if (subNavId != null) putInt(KEY1, subNavId)
                },
            )
        }
    }
}