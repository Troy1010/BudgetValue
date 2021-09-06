package com.tminus1010.budgetvalue.replay_or_future

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.extensions.bind
import com.tminus1010.budgetvalue._core.extensions.viewItemRecipe
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.bindItemHeaderBinding
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.bindItemTextViewBinding
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue.databinding.FragFuturesReviewBinding
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
                        viewItemRecipe(bindItemHeaderBinding, futuresReviewVM.nameHeader),
                        viewItemRecipe(bindItemHeaderBinding, futuresReviewVM.terminationStatusHeader),
                    ),
                    *it.map {
                        listOf(
                            viewItemRecipe(bindItemTextViewBinding, it.name),
                            viewItemRecipe(bindItemTextViewBinding, it.terminationStatus.displayStr),
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