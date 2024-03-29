package com.tminus1010.buva.ui.budget.budget

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.android.viewBinding
import com.tminus1010.buva.databinding.ItemTmTableViewBinding
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BudgetFrag : Fragment(R.layout.item_tm_table_view) {
    private val vb by viewBinding(ItemTmTableViewBinding::bind)
    private val viewModel: BudgetVM by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # State
        vb.tmTableView.bind(viewModel.tableViewVMItem) { it.bind(this) }
    }
}