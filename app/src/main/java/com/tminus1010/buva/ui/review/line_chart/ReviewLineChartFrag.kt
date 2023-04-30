package com.tminus1010.buva.ui.review_spend_bar_chart

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.tminus1010.buva.R
import com.tminus1010.buva.databinding.FragReviewLineChartBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewLineChartFrag : Fragment(R.layout.frag_review_line_chart) {
    lateinit var vb: FragReviewLineChartBinding
    val reviewLineChartVM by activityViewModels<ReviewLineChartVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragReviewLineChartBinding.bind(view)
        reviewLineChartVM.lineChartVMItem.bind(vb.lineChart1)
    }
}