package com.tminus1010.buva.ui.review_bar_chart

import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.tminus1010.buva.app.TransactionsInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
@HiltViewModel
class ReviewBarChartVM @Inject constructor(
    transactionsInteractor: TransactionsInteractor,
) : ViewModel() {
    val barData =
        flowOf(
            BarData(BarDataSet(listOf(BarEntry(1.0f, 1.0f), BarEntry(2.0f, 2.0f), BarEntry(3.0f, 3.0f)), "some data"))
        )
}