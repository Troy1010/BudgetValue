package com.tminus1010.buva.ui.review

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.buva.R
import com.tminus1010.buva.databinding.FragReviewHostBinding
import com.tminus1010.buva.ui.history.HistoryFrag
import com.tminus1010.buva.ui.review_bar_chart.ReviewBarChartFrag
import com.tminus1010.buva.ui.review_pie_chart.ReviewPieChartFrag
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewHostFrag : Fragment(R.layout.frag_review_host) {
    lateinit var vb: FragReviewHostBinding
    val reviewHostVM by viewModels<ReviewHostVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragReviewHostBinding.bind(view)
        vb.frame.bind(reviewHostVM.frag) {
            this@ReviewHostFrag.childFragmentManager
                .beginTransaction()
                .replace(
                    R.id.frame,
                    when (it) {
                        ReviewPieChartFrag::class.java -> ReviewPieChartFrag()
                        HistoryFrag::class.java -> HistoryFrag()
                        ReviewBarChartFrag::class.java -> ReviewBarChartFrag()
                        else -> error("Unhandled Fragment")
                    },
                )
                .commitNowAllowingStateLoss()
        }
        vb.buttonsview.bind(reviewHostVM.buttons) { buttons = it }
    }
}