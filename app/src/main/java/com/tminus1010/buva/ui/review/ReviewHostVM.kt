package com.tminus1010.buva.ui.review

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.data.SelectedReviewHostPage
import com.tminus1010.buva.ui.review.history.HistoryFrag
import com.tminus1010.buva.ui.review.review_pie_chart.ReviewPieChartFrag
import com.tminus1010.buva.ui.review.review_spend_bar_chart.ReviewSpendBarChartFrag
import com.tminus1010.buva.ui.review_spend_bar_chart.ReviewTotalLineChartFrag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ReviewHostVM @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val selectedReviewHostPage: SelectedReviewHostPage,
) : ViewModel() {
    // # User Intent
    fun userSelectMenuItem(id: Int) {
        selectedReviewHostPage.set(id)
    }

    // # Private
    init {
        savedStateHandle.get<Int>(KEY1)?.also { selectedReviewHostPage.set(it) }
    }

    // # State
    val selectedItemId = selectedReviewHostPage.flow
    val fragFactory =
        selectedItemId.map {
            when (it) {
                R.id.spendPieChart -> {
                    { ReviewPieChartFrag() }
                }
                R.id.history -> {
                    { HistoryFrag() }
                }
                R.id.spendBarChart -> {
                    { ReviewSpendBarChartFrag() }
                }
                R.id.totalLineChart -> {
                    { ReviewTotalLineChartFrag() }
                }
                else -> error("Unknown id")
            }
        }
}