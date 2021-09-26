package com.tminus1010.budgetvalue.all.presentation_and_view.review

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.all.presentation_and_view._models.NoMostRecentSpend
import com.tminus1010.budgetvalue.all.presentation_and_view._models.bind
import com.tminus1010.budgetvalue.all.presentation_and_view.bind
import com.tminus1010.budgetvalue.databinding.FragReviewBinding
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
        // # Events
        reviewVM.errors.observe(viewLifecycleOwner) {
            when (it) {
                is NoMostRecentSpend -> logz("Swallowing error:${it.javaClass.simpleName}")
                else -> easyToast("An error occurred").run { logz("error:", it) }
            }
        }
        // # State
        vb.pieChart1.bind(reviewVM.pieChartVMItem)
        vb.spinnerDuration.bind(reviewVM.spinnerVMItem)
    }
}