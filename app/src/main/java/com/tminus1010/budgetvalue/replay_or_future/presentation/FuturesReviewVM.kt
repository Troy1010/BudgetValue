package com.tminus1010.budgetvalue.replay_or_future.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.history.presentation.TextPresentationModel
import com.tminus1010.budgetvalue.replay_or_future.data.FuturesRepo
import com.tminus1010.budgetvalue.replay_or_future.domain.BasicFuture
import com.tminus1010.budgetvalue.replay_or_future.domain.TotalFuture
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FuturesReviewVM @Inject constructor(
    futuresRepo: FuturesRepo,
) : ViewModel() {
    // # State
    val recipeGrid =
        futuresRepo.fetchFutures()
            .map {
                listOf(
                    listOf(
                        TextPresentationModel(TextPresentationModel.Style.HEADER, "Name"),
                        TextPresentationModel(TextPresentationModel.Style.HEADER, "Status"),
                        TextPresentationModel(TextPresentationModel.Style.HEADER, "Search by"),
                    ),
                    *it.map {
                        listOf(
                            TextPresentationModel(TextPresentationModel.Style.TWO, it.name),
                            TextPresentationModel(TextPresentationModel.Style.TWO, it.terminationStatus.displayStr),
                            TextPresentationModel(
                                TextPresentationModel.Style.TWO,
                                when (it) {
                                    is BasicFuture -> it.searchText.take(10)
                                    is TotalFuture -> it.searchTotal.toString()
                                    else -> error("Unhandled IFuture:$it")
                                },
                            ),
                        )
                    }.toTypedArray()
                )
            }
}