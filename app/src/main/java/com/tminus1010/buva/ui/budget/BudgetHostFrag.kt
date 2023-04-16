package com.tminus1010.buva.ui.budget

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.extensions.isSettingSelectedItemId
import com.tminus1010.buva.databinding.FragBudgetHostBinding
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BudgetHostFrag : Fragment(R.layout.frag_budget_host) {
    lateinit var vb: FragBudgetHostBinding
    val viewModel by viewModels<BudgetHostVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragBudgetHostBinding.bind(view)
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
            this@BudgetHostFrag.childFragmentManager
                .beginTransaction()
                .replace(id, fragFactory())
                .commitNowAllowingStateLoss()
        }
    }
}