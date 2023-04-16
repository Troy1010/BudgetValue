package com.tminus1010.buva.ui.review

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.extensions.isSettingSelectedItemId
import com.tminus1010.buva.databinding.FragReviewHostBinding
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewHostFrag : Fragment(R.layout.frag_review_host) {
    lateinit var vb: FragReviewHostBinding
    val viewModel by viewModels<ReviewHostVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragReviewHostBinding.bind(view)
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
            this@ReviewHostFrag.childFragmentManager
                .beginTransaction()
                .replace(id, fragFactory())
                .commitNowAllowingStateLoss()
        }
    }
}