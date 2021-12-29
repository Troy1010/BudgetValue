package com.tminus1010.budgetvalue.replay_or_future.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.framework.view.recipe_factories.itemTextViewRB
import com.tminus1010.budgetvalue._core.framework.view.viewBinding
import com.tminus1010.budgetvalue.databinding.FragReplaysBinding
import com.tminus1010.budgetvalue.replay_or_future.app.ReplayInteractor
import com.tminus1010.budgetvalue.replay_or_future.data.ReplaysRepo
import com.tminus1010.budgetvalue.replay_or_future.presentation.ReplaysVM
import com.tminus1010.budgetvalue.transactions.app.interactor.TransactionsInteractor
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@AndroidEntryPoint
class UseReplayFrag : Fragment(R.layout.frag_replays) {
    private val vb by viewBinding(FragReplaysBinding::bind)

    @Inject
    lateinit var replaysRepo: ReplaysRepo

    @Inject
    lateinit var replayInteractor: ReplayInteractor

    @Inject
    lateinit var transactionsInteractor: TransactionsInteractor
    val replaysVM by viewModels<ReplaysVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //
        replaysVM.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        // # TMTableView
        replaysVM.replays
            .observeOn(Schedulers.computation())
            .map { replays ->
                replays.map { replay ->
                    listOf(
                        itemTextViewRB().create(replay.name) {
                            replayInteractor.useReplayOnTransaction(replay, transactionsInteractor.mostRecentUncategorizedSpendFlow.value!!).subscribe()
                            nav.navigateUp()
                        }
                    )
                }
            }
            .observe(viewLifecycleOwner) { recipes2D ->
                vb.tmTableView.initialize(
                    recipeGrid = recipes2D,
                    shouldFitItemWidthsInsideTable = true,
                )
            }
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.futuresReviewFrag)
        }
    }
}