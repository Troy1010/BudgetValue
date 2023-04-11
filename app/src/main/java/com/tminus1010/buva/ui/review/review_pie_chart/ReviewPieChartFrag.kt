package com.tminus1010.buva.ui.review.review_pie_chart

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.extensions.bind
import com.tminus1010.buva.all_layers.extensions.onClick
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.databinding.FragReviewPieChartBinding
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewPieChartFrag : Fragment(R.layout.frag_review_pie_chart) {
    lateinit var vb: FragReviewPieChartBinding
    val reviewPieChartVM by viewModels<ReviewPieChartVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragReviewPieChartBinding.bind(view)
        // # State
        vb.pieChart1.bind(reviewPieChartVM.pieChartVMItem)
        reviewPieChartVM.selectableDurationSpinnerVMItem.bind(vb.spinnerDuration)
        reviewPieChartVM.usePeriodTypeSpinnerVMItem.bind(vb.spinnerUsePeriodType)
        vb.tvTitle.bind(reviewPieChartVM.title) { text = it }
        vb.ivLeft.bind(reviewPieChartVM.leftVisibility) { visibility = it }
        vb.ivRight.bind(reviewPieChartVM.isRightVisible) { visibility = it }
        // # User Intent
        vb.ivLeft.onClick { reviewPieChartVM.userPrevious.onNext(Unit) }
        vb.ivRight.onClick { reviewPieChartVM.userNext.onNext(Unit) }
    }
}