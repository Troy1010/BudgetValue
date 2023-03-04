package com.tminus1010.buva.ui.review

import androidx.lifecycle.ViewModel
import com.tminus1010.buva.ui.review_pie_chart.ReviewPieChartFrag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class ReviewHostVM @Inject constructor(
) : ViewModel() {
    val frag = flowOf(ReviewPieChartFrag())
}