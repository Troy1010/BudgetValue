package com.tminus1010.budgetvalue.replay_or_future.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.all.extensions.bind
import com.tminus1010.budgetvalue._core.middleware.view.recipe_factories.itemHeaderRF
import com.tminus1010.budgetvalue._core.middleware.view.recipe_factories.itemTextViewRB
import com.tminus1010.budgetvalue._core.middleware.view.viewBinding
import com.tminus1010.budgetvalue.databinding.FragFuturesReviewBinding
import com.tminus1010.budgetvalue.replay_or_future.domain.BasicFuture
import com.tminus1010.budgetvalue.replay_or_future.domain.TotalFuture
import com.tminus1010.budgetvalue.replay_or_future.presentation.FuturesReviewVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FuturesReviewFrag : Fragment(R.layout.frag_futures_review) {
    private val vb by viewBinding(FragFuturesReviewBinding::bind)
    private val futuresReviewVM by viewModels<FuturesReviewVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb.tmTableViewFutures.bind(futuresReviewVM.futures) {
            initialize(
                listOf(
                    listOf(
                        itemHeaderRF().create(futuresReviewVM.nameHeader),
                        itemHeaderRF().create(futuresReviewVM.terminationStatusHeader),
                        itemHeaderRF().create(futuresReviewVM.searchByHeader),
                    ),
                    *it.map {
                        listOf(
                            itemTextViewRB().create(it.name),
                            itemTextViewRB().style(10).create(it.terminationStatus.displayStr),
                            itemTextViewRB().create(
                                when (it) {
                                    is BasicFuture -> it.searchText.take(10)
                                    is TotalFuture -> it.searchTotal.toString()
                                    else -> error("Unhandled IFuture:$it")
                                }
                            )
                        )
                    }.toTypedArray()
                ),
                shouldFitItemWidthsInsideTable = true,
                rowFreezeCount = 1,
            )
        }
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.futuresReviewFrag)
        }
    }
}