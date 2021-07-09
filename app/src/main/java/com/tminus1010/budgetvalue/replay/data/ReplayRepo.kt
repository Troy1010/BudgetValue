package com.tminus1010.budgetvalue.replay.data

import com.tminus1010.budgetvalue._core.data.MiscDAO
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.replay.models.Replay
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class ReplayRepo @Inject constructor(
    private val miscDAO: MiscDAO,
    private val categoryAmountsConverter: CategoryAmountsConverter
) {
    fun add(replay: Replay): Completable =
        miscDAO.add(replay.toDTO(categoryAmountsConverter)).subscribeOn(Schedulers.io())

    fun fetchReplays(): Observable<List<Replay>> =
        miscDAO.fetchReplays().subscribeOn(Schedulers.io())
            .map { it.map { Replay.fromDTO(it, categoryAmountsConverter) } }
}