package com.tminus1010.budgetvalue.review.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.all.extensions.bind
import com.tminus1010.budgetvalue._core.all.extensions.easyVisibility
import com.tminus1010.budgetvalue._core.all.extensions.onClick
import com.tminus1010.budgetvalue.all.framework.extensions.invoke
import com.tminus1010.budgetvalue.all.presentation_and_view._models.bind
import com.tminus1010.budgetvalue.all.presentation_and_view.bind
import com.tminus1010.budgetvalue.databinding.FragReviewBinding
import com.tminus1010.budgetvalue.review.presentation.NoMoreDataException
import com.tminus1010.budgetvalue.review.presentation.NoMostRecentSpendException
import com.tminus1010.budgetvalue.review.presentation.ReviewVM
import com.tminus1010.budgetvalue.review.presentation.TooFarBackException
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.view.extensions.easyToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewFrag : Fragment(R.layout.frag_review) {
    lateinit var vb: FragReviewBinding
    val reviewVM by viewModels<ReviewVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragReviewBinding.bind(view)
        reviewVM.apply {
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
            vb.pieChart1.bind(pieChartVMItem)
            vb.spinnerDuration.bind(selectableDurationSpinnerVMItem)
            vb.spinnerUsePeriodType.bind(usePeriodTypeSpinnerVMItem)
            vb.tvTitle.bind(title) { text = it }
            vb.ivLeft.bind(isLeftVisible) { easyVisibility = it }
            vb.ivRight.bind(isRightVisible) { easyVisibility = it }
            // # UserIntents
            vb.ivLeft.onClick(userPrevious::invoke)
            vb.ivRight.onClick(userNext::invoke)
        }
    }
}