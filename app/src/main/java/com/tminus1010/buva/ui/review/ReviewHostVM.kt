package com.tminus1010.buva.ui.review

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.buva.ui.history.HistoryFrag
import com.tminus1010.buva.ui.importZ.ImportFrag
import com.tminus1010.buva.ui.review_pie_chart.ReviewPieChartFrag
import com.tminus1010.buva.ui.review_spend_bar_chart.ReviewSpendBarChartFrag
import com.tminus1010.buva.ui.review_spend_bar_chart.ReviewTotalLineChartFrag
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

    fun userViewReviewSpendBarChart() {
        frag.onNext(ReviewSpendBarChartFrag::class.java)
    }

    fun userViewTotalLineChart() {
        frag.onNext(ReviewTotalLineChartFrag::class.java)
    }

    // # State
    val frag = MutableStateFlow<Class<out Fragment>>(ReviewPieChartFrag::class.java)
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
                    title = "Spend Bar Chart",
                    onClick = { userViewReviewSpendBarChart() }
                ),
                ButtonVMItem(
                    title = "Total Line Chart",
                    onClick = { userViewTotalLineChart() }
                ),
            )
        )
}