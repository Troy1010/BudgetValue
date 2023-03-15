package com.tminus1010.buva.ui.review_bar_chart

import androidx.lifecycle.ViewModel
import com.tminus1010.buva.app.TransactionsInteractor
import com.tminus1010.buva.ui.all_features.view_model_item.BarChartVMItem
import com.tminus1010.tmcommonkotlin.core.extensions.toDisplayStr
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.math.absoluteValue

@HiltViewModel
class ReviewBarChartVM @Inject constructor(
    transactionsInteractor: TransactionsInteractor,
) : ViewModel() {
    private val valuesAndLabels =
        transactionsInteractor.spendBlocks.map { transactionBlocks ->
            transactionBlocks
                .sortedBy { it.datePeriod?.startDate }
                .map { Pair(it.total.toFloat().absoluteValue, it.datePeriod?.startDate?.toDisplayStr() ?: "") }
        }
    val barDataVMItem =
        BarChartVMItem(
            valuesAndLabels = valuesAndLabels,
        )
}