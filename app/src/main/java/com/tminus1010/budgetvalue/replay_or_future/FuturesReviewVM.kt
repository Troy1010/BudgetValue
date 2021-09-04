package com.tminus1010.budgetvalue.replay_or_future

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.replay_or_future.data.FuturesRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

//@HiltViewModel
//class FuturesReviewVM @Inject constructor(
//    private val futuresRepo: FuturesRepo,
//) : ViewModel() {
//    val futures =
//        futuresRepo.fetchFutures()
//}

@HiltViewModel
class FuturesReviewVM @Inject constructor(
    private val futuresRepo: FuturesRepo,
) : ViewModel() {
    val futures =
        futuresRepo.fetchFutures()
}