package com.tminus1010.budgetvalue.auto_replay.data

import com.tminus1010.budgetvalue._core.data.MiscDAO
import com.tminus1010.budgetvalue.auto_replay.models.AutoReplay
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class AutoReplayRepo @Inject constructor(
    private val miscDAO: MiscDAO,
    private val categoryAmountsConverter: CategoryAmountsConverter
) {
    fun add(autoReplay: AutoReplay): Completable =
        miscDAO.add(autoReplay.toDTO(categoryAmountsConverter)).subscribeOn(Schedulers.io())

    fun fetchAutoReplays(): Observable<List<AutoReplay>> =
        miscDAO.fetchAutoReplays().subscribeOn(Schedulers.io())
            .map { it.map { AutoReplay.fromDTO(it, categoryAmountsConverter) } }
}