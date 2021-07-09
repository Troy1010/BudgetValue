package com.tminus1010.budgetvalue.replay.data

import com.tminus1010.budgetvalue._core.data.MiscDAO
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.replay.models.BasicReplay
import com.tminus1010.budgetvalue.replay.models.IReplay
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class ReplayRepo @Inject constructor(
    private val miscDAO: MiscDAO,
    private val categoryAmountsConverter: CategoryAmountsConverter
) {
    fun add(basicReplay: BasicReplay): Completable =
        miscDAO.add(basicReplay.toDTO(categoryAmountsConverter)).subscribeOn(Schedulers.io())

    fun fetchReplays(): Observable<List<IReplay>> =
        miscDAO.fetchBasicReplays().subscribeOn(Schedulers.io())
            .map { it.map { BasicReplay.fromDTO(it, categoryAmountsConverter) } }
}