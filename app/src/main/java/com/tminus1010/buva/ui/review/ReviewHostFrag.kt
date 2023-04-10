package com.tminus1010.buva.ui.review

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.buva.R
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
        vb.fragmentcontainerview.bind(viewModel.frag) { fragFactory ->
            this@ReviewHostFrag.childFragmentManager
                .beginTransaction()
                .replace(id, fragFactory())
                .commitNowAllowingStateLoss()
        }
        vb.bottomnavigationview.setOnItemSelectedListener { viewModel.userSelectMenuItem(it.itemId); true }
    }
}