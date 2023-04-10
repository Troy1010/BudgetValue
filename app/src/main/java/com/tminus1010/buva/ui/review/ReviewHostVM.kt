package com.tminus1010.buva.ui.review

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.ui.review.history.HistoryFrag
import com.tminus1010.buva.ui.review.review_pie_chart.ReviewPieChartFrag
import com.tminus1010.buva.ui.review.review_spend_bar_chart.ReviewSpendBarChartFrag
import com.tminus1010.buva.ui.review_spend_bar_chart.ReviewTotalLineChartFrag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ReviewHostVM @Inject constructor(
) : ViewModel() {
    // # User Intent
    fun userSelectMenuItem(id: Int) {
        when (id) {
            R.id.spendPieChart ->
                frag.onNext(ReviewPieChartFrag::class.java)
            R.id.history ->
                frag.onNext(HistoryFrag::class.java)
            R.id.spendBarChart ->
                frag.onNext(ReviewSpendBarChartFrag::class.java)
            R.id.totalLineChart ->
                frag.onNext(ReviewTotalLineChartFrag::class.java)
            else -> error("Unknown id")
        }
    }

    // # State
    val frag = MutableStateFlow<Class<out Fragment>>(HistoryFrag::class.java)
}