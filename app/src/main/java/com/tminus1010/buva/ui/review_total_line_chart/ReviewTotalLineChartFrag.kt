package com.tminus1010.buva.ui.review_spend_bar_chart

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.buva.R
import com.tminus1010.buva.databinding.FragReviewTotalLineChartBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewTotalLineChartFrag : Fragment(R.layout.frag_review_total_line_chart) {
    lateinit var vb: FragReviewTotalLineChartBinding
    val reviewTotalLineChartVM by viewModels<ReviewTotalLineChartVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragReviewTotalLineChartBinding.bind(view)
        reviewTotalLineChartVM.lineChartVMItem.bind(vb.lineChart1)
    }
}