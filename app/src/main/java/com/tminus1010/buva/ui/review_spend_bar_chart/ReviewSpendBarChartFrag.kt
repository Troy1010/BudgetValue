package com.tminus1010.buva.ui.review_spend_bar_chart

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.buva.R
import com.tminus1010.buva.databinding.FragReviewSpendBarChartBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewSpendBarChartFrag : Fragment(R.layout.frag_review_spend_bar_chart) {
    lateinit var vb: FragReviewSpendBarChartBinding
    val reviewSpendBarChartVM by viewModels<ReviewSpendBarChartVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragReviewSpendBarChartBinding.bind(view)
        reviewSpendBarChartVM.barDataVMItem.bind(vb.barChart1)
    }
}