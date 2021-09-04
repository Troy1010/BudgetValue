package com.tminus1010.budgetvalue.replay_or_future

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R

class FuturesReviewFrag: Fragment(R.layout.frag_futures_review) {

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.futuresReviewFrag)
        }
    }
}