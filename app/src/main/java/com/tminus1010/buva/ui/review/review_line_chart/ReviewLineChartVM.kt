package com.tminus1010.buva.ui.review_spend_bar_chart

import androidx.lifecycle.ViewModel
import com.tminus1010.buva.app.TransactionsInteractor
import com.tminus1010.buva.data.AccountsRepo
import com.tminus1010.buva.data.ReconciliationsRepo
import com.tminus1010.buva.domain.MiscUtil
import com.tminus1010.buva.ui.all_features.view_model_item.LineChartVMItem
import com.tminus1010.tmcommonkotlin.core.extensions.toDisplayStr
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ReviewLineChartVM @Inject constructor(
    transactionsInteractor: TransactionsInteractor,
    accountsRepo: AccountsRepo,
    reconciliationsRepo: ReconciliationsRepo,
) : ViewModel() {
    private val valuesAndLabels =
        transactionsInteractor.transactionBlocks.map { transactionBlocks ->
            transactionBlocks
                .sortedBy { it.datePeriod?.startDate }
                .map {
                    Pair(
                        MiscUtil.guessAccountsTotalInPast(it.datePeriod!!.endDate, accountsRepo.accountsAggregate.first(), transactionsInteractor.transactionBlocks.first(), reconciliationsRepo.reconciliations.first()).toFloat(),
                        it.datePeriod.startDate.toDisplayStr(),
                    )
                }
        }
    val lineChartVMItem =
        LineChartVMItem(
            valuesAndLabels = valuesAndLabels,
        )
}