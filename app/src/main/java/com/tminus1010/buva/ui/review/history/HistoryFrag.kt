package com.tminus1010.buva.ui.review.history

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.android.viewBinding
import com.tminus1010.buva.all_layers.extensions.show
import com.tminus1010.buva.databinding.FragHistoryBinding
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import com.tminus1010.tmcommonkotlin.rx3.extensions.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFrag : Fragment(R.layout.frag_history) {
    private val historyVM by activityViewModels<HistoryVM>()
    private val vb by viewBinding(FragHistoryBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Events
        historyVM.showPopupMenu.observe(viewLifecycleOwner) { (view, menuItems) -> PopupMenu(requireActivity(), view).show(menuItems) }
        // # State
        vb.tmTableViewHistory.bind(historyVM.historyTableView) { it.bind(this) }
    }
}