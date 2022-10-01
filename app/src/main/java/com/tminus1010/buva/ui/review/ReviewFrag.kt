package com.tminus1010.buva.ui.review

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.extensions.bind
import com.tminus1010.buva.all_layers.extensions.onClick
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.databinding.FragReviewBinding
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewFrag : Fragment(R.layout.frag_review) {
    lateinit var vb: FragReviewBinding
    val reviewVM by viewModels<ReviewVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragReviewBinding.bind(view)
        // # State
        vb.pieChart1.bind(reviewVM.pieChartVMItem)
        reviewVM.selectableDurationSpinnerVMItem.bind(vb.spinnerDuration)
        reviewVM.usePeriodTypeSpinnerVMItem.bind(vb.spinnerUsePeriodType)
        vb.tvTitle.bind(reviewVM.title) { text = it }
        vb.ivLeft.bind(reviewVM.leftVisibility) { visibility = it }
        vb.ivRight.bind(reviewVM.isRightVisible) { visibility = it }
        // # UserIntents
        vb.ivLeft.onClick { reviewVM.userPrevious.onNext(Unit) }
        vb.ivRight.onClick { reviewVM.userNext.onNext(Unit) }
    }
}