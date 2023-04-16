package com.tminus1010.buva.ui.importZ

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.all_layers.extensions.isSettingSelectedItemId
import com.tminus1010.buva.databinding.FragImportHostBinding
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImportHostFrag : Fragment(R.layout.frag_import_host) {
    lateinit var vb: FragImportHostBinding
    val viewModel by viewModels<ImportHostVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragImportHostBinding.bind(view)
        // # User Intent
        vb.bottomnavigationview.setOnItemSelectedListener {
            if (!vb.bottomnavigationview.isSettingSelectedItemId) {
                viewModel.userSelectMenuItem(it.itemId)
                false
            } else {
                true
            }
        }
        // # State
        vb.bottomnavigationview.bind(viewModel.selectedItemId) { isSettingSelectedItemId = true; selectedItemId = it; isSettingSelectedItemId = false }
        vb.fragmentcontainerview.bind(viewModel.fragFactory) { fragFactory ->
            this@ImportHostFrag.childFragmentManager
                .beginTransaction()
                .replace(id, fragFactory())
                .commitNowAllowingStateLoss()
        }
    }

    companion object {
        fun navTo(nav: NavController, subNavId: Int? = null) {
            nav.navigate(
                R.id.importHostFrag,
                Bundle().apply {
                    if (subNavId != null) putInt(KEY1, subNavId)
                },
            )
        }
    }
}