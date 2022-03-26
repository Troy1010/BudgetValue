package com.tminus1010.budgetvalue.ui.review

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.all_layers.extensions.onClick
import com.tminus1010.budgetvalue.all.presentation.extensions.bind
import com.tminus1010.budgetvalue.databinding.FragReviewBinding
import com.tminus1010.budgetvalue.ui.review.presentation.NoMoreDataException
import com.tminus1010.budgetvalue.ui.review.presentation.NoMostRecentSpendException
import com.tminus1010.budgetvalue.ui.review.presentation.TooFarBackException
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.view.extensions.easyToast
import com.tminus1010.tmcommonkotlin.view.extensions.easyVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewFrag : Fragment(R.layout.frag_review) {
    lateinit var vb: FragReviewBinding
    val reviewVM by viewModels<ReviewVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragReviewBinding.bind(view)
        // # Events
        reviewVM.errors.observe(viewLifecycleOwner) {
            when (it) {
                is NoMostRecentSpendException -> logz("Swallowing error:${it.javaClass.simpleName}")
                is NoMoreDataException -> easyToast("No more data. Import more transactions")
                is TooFarBackException -> easyToast("No more data")
                else -> easyToast("An error occurred").run { logz("error:", it) }
            }
        }
        // # State
        vb.pieChart1.bind(reviewVM.pieChartVMItem)
        reviewVM.selectableDurationSpinnerVMItem.bind(vb.spinnerDuration)
        reviewVM.usePeriodTypeSpinnerVMItem.bind(vb.spinnerUsePeriodType)
        vb.tvTitle.bind(reviewVM.title) { text = it }
        vb.ivLeft.bind(reviewVM.isLeftVisible) { easyVisibility = it }
        vb.ivRight.bind(reviewVM.isRightVisible) { easyVisibility = it }
        // # View Events -> UserIntents
        vb.ivLeft.onClick { reviewVM.userPrevious.onNext(Unit) }
        vb.ivRight.onClick { reviewVM.userNext.onNext(Unit) }
    }
}