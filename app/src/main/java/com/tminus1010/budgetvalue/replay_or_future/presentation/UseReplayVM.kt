package com.tminus1010.budgetvalue.replay_or_future.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.easyEmit
import com.tminus1010.budgetvalue.all_features.framework.view.Toaster
import com.tminus1010.budgetvalue.all_features.presentation.model.MenuPresentationModel
import com.tminus1010.budgetvalue.all_features.presentation.model.MenuVMItem
import com.tminus1010.budgetvalue.replay_or_future.app.ReplayInteractor
import com.tminus1010.budgetvalue.replay_or_future.data.ReplaysRepo
import com.tminus1010.budgetvalue.replay_or_future.domain.BasicReplay
import com.tminus1010.budgetvalue.replay_or_future.domain.IReplay
import com.tminus1010.budgetvalue.transactions.app.interactor.TransactionsInteractor
import com.tminus1010.budgetvalue.transactions.app.use_case.CategorizeAllMatchingUncategorizedTransactions
import com.tminus1010.budgetvalue.all_features.presentation.model.TextVMItem
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class UseReplayVM @Inject constructor(
    replaysRepo: ReplaysRepo,
    private val replayInteractor: ReplayInteractor,
    private val transactionsInteractor: TransactionsInteractor,
    private val categorizeAllMatchingUncategorizedTransactions: CategorizeAllMatchingUncategorizedTransactions,
    private val toaster: Toaster,
) : ViewModel() {
    fun chooseReplay(replay: IReplay) {
        replayInteractor.useReplayOnTransaction(replay, transactionsInteractor.mostRecentUncategorizedSpend2.value!!).subscribe()
        navUp.easyEmit(Unit)
    }

    fun chooseReplayAndApplyToAllMatching(replay: IReplay) {
        categorizeAllMatchingUncategorizedTransactions(
            predicate = { transactionsInteractor.mostRecentUncategorizedSpend2.value!!.description.uppercase() in it.description.uppercase() },
            categorization = {
                when (replay) {
                    is BasicReplay -> replay.categorize(it)
                    else -> error("Unhandled replay type:$replay")
                }
            }
        ).subscribeBy { toaster.toast("$it transactions categorized") }
        navUp.easyEmit(Unit)
    }

    // # State
    val replays =
        replaysRepo.replays
            .map { replays ->
                replays.map { replay ->
                    TextVMItem(
                        text1 = replay.name,
                        onClick = { chooseReplay(replay) },
                        menuPresentationModel = MenuPresentationModel(
                            MenuVMItem(
                                title = "Use on all Uncategorized with matching description",
                                onClick = { chooseReplayAndApplyToAllMatching(replay) }
                            )
                        )
                    )
                }
            }

    // # Presentation Events
    val navUp = MutableSharedFlow<Unit>()
}