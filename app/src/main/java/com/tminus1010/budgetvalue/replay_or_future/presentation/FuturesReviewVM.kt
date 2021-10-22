package com.tminus1010.budgetvalue.replay_or_future.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.replay_or_future.data.FuturesRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FuturesReviewVM @Inject constructor(
    futuresRepo: FuturesRepo,
) : ViewModel() {
    val futures =
        futuresRepo.fetchFutures()
            .map { it.sortedBy { it.terminationStatus.ordinal } }!!
    val nameHeader = "Name"
    val terminationStatusHeader = "Status"
    val searchByHeader = "Search by"
}