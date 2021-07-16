package com.tminus1010.budgetvalue.replay_or_future.data

import com.tminus1010.budgetvalue._core.data.MiscDAO
import com.tminus1010.budgetvalue.categories.CategoryAmountFormulasConverter
import com.tminus1010.budgetvalue.categories.ICategoryParser
import com.tminus1010.budgetvalue.replay_or_future.models.BasicReplay
import com.tminus1010.budgetvalue.replay_or_future.models.IReplay
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class ReplaysRepo @Inject constructor(
    private val miscDAO: MiscDAO,
    private val categoryAmountFormulasConverter: CategoryAmountFormulasConverter,
    private val categoryParser: ICategoryParser,
) {
    fun add(basicReplay: BasicReplay): Completable =
        miscDAO.add(basicReplay.toDTO(categoryAmountFormulasConverter)).subscribeOn(Schedulers.io())

    fun delete(basicReplayName: String): Completable =
        miscDAO.delete(basicReplayName).subscribeOn(Schedulers.io())

    fun fetchReplays(): Observable<List<IReplay>> =
        miscDAO.fetchBasicReplays().subscribeOn(Schedulers.io())
            .map { it.map { BasicReplay.fromDTO(it, categoryAmountFormulasConverter, categoryParser) } }
}