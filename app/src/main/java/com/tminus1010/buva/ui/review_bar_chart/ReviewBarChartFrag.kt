package com.tminus1010.buva.ui.review_bar_chart

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.buva.R
import com.tminus1010.buva.databinding.FragReviewBarChartBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewBarChartFrag : Fragment(R.layout.frag_review_bar_chart) {
    lateinit var vb: FragReviewBarChartBinding
    val reviewPieChartVM by viewModels<ReviewBarChartVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragReviewBarChartBinding.bind(view)
        reviewPieChartVM.barDataVMItem.bind(vb.barChart1)
    }
}