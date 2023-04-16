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
    val viewModel by viewModels<ReviewPieChartVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragReviewPieChartBinding.bind(view)
        // # User Intent
        vb.ivLeft.onClick { viewModel.userPrevious.onNext(Unit) }
        vb.ivRight.onClick { viewModel.userNext.onNext(Unit) }
        vb.dots.onSelectListener = { viewModel.userClickDot.onNext(it) }
        // # State
        vb.pieChart1.bind(viewModel.pieChartVMItem)
        viewModel.selectableDurationSpinnerVMItem.bind(vb.spinnerDuration)
        viewModel.usePeriodTypeSpinnerVMItem.bind(vb.spinnerUsePeriodType)
        vb.tvTitle.bind(viewModel.title) { text = it }
        vb.ivLeft.bind(viewModel.leftVisibility) { visibility = it }
        vb.ivRight.bind(viewModel.isRightVisible) { visibility = it }
        vb.dots.bind(viewModel.dotCount) { initDots(it) }
        vb.dots.bind(viewModel.selectedDot) { i -> runCatching { setDotSelection(i) }.onFailure { logz("setDotSelection failure. i:$i error: ${it::class.java.simpleName}") } }
    }
}