package com.tminus1010.buva.ui.review_bar_chart

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.tminus1010.buva.R
import com.tminus1010.buva.databinding.FragReviewBarChartBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewBarChartFrag : Fragment(R.layout.frag_review_bar_chart) {
    lateinit var vb: FragReviewBarChartBinding
    val reviewPieChartVM by viewModels<ReviewBarChartVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragReviewBarChartBinding.bind(view)
        vb.barChart1.data = BarData(BarDataSet(listOf(BarEntry(1.0f, 1.0f), BarEntry(2.0f, 2.0f), BarEntry(3.0f, 3.0f)), "some data"))
    }
}