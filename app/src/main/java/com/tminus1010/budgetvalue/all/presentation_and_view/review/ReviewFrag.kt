package com.tminus1010.budgetvalue.all.presentation_and_view.review

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.extensions.bind
import com.tminus1010.budgetvalue.all.presentation_and_view.services.createPieData
import com.tminus1010.budgetvalue.databinding.FragReviewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewFrag : Fragment(R.layout.frag_review) {
    lateinit var vb: FragReviewBinding
    val reviewVM by viewModels<ReviewVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragReviewBinding.bind(view)
        vb.pieChart1.bind(reviewVM.chartData) { data = createPieData(it); invalidate() }
    }
}