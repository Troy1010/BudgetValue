package com.tminus1010.budgetvalue._unrestructured.replay_or_future.presentation

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._unrestructured.replay_or_future.data.ReplaysRepo
import com.tminus1010.budgetvalue._unrestructured.replay_or_future.domain.BasicReplay
import com.tminus1010.budgetvalue._unrestructured.replay_or_future.domain.IReplay
import com.tminus1010.budgetvalue._unrestructured.transactions.app.interactor.TransactionsInteractor
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

@HiltViewModel
class ReplaysVM @Inject constructor(
    private val replaysRepo: ReplaysRepo,
    transactionsInteractor: TransactionsInteractor,
) : ViewModel() {
    // # Input
    fun userAddSearchTextToReplay(replay: IReplay) {
        if (searchText == null) return
        when (replay) {
            is BasicReplay -> replaysRepo.update(replay.copy(searchTexts = replay.searchTexts.plus(searchText)))
            else -> error("unhandled type")
        }
            .andThen(Completable.fromAction { navUp.onNext(Unit) })
            .subscribe()
    }

    // # Output
    val searchText = transactionsInteractor.mostRecentUncategorizedSpend.value!!.first?.description
    val replays: Observable<List<IReplay>> =
        replaysRepo.fetchReplays()
    val navUp = PublishSubject.create<Unit>()!!
}