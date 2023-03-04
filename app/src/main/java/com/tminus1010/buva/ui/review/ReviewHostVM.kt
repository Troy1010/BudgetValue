package com.tminus1010.buva.ui.review

import androidx.lifecycle.ViewModel
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.buva.ui.history.HistoryFrag
import com.tminus1010.buva.ui.review_bar_chart.ReviewBarChartFrag
import com.tminus1010.buva.ui.review_pie_chart.ReviewPieChartFrag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class ReviewHostVM @Inject constructor(
) : ViewModel() {
    // # UserIntents
    fun userViewReviewPieChart() {
        frag.onNext(ReviewPieChartFrag::class.java)
    }

    fun userViewHistory() {
        frag.onNext(HistoryFrag::class.java)
    }

    fun userViewBarChart() {
        frag.onNext(ReviewBarChartFrag::class.java)
    }

    // # State
    val frag = MutableStateFlow<Any>(ReviewPieChartFrag::class.java)
    val buttons =
        flowOf(
            listOf(
                ButtonVMItem(
                    title = "Pie Chart",
                    onClick = { userViewReviewPieChart() }
                ),
                ButtonVMItem(
                    title = "History",
                    onClick = { userViewHistory() }
                ),
                ButtonVMItem(
                    title = "Bar Chart",
                    onClick = { userViewBarChart() }
                ),
            )
        )
}