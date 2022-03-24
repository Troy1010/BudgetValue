package com.tminus1010.budgetvalue.replay_or_future.data

import com.tminus1010.budgetvalue.all_features.data.service.MiscDAO
import com.tminus1010.budgetvalue.categories.CategoryAmountFormulasConverter
import com.tminus1010.budgetvalue.categories.domain.CategoriesInteractor
import com.tminus1010.budgetvalue.replay_or_future.domain.BasicReplay
import com.tminus1010.budgetvalue.replay_or_future.domain.IReplay
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.rx3.asFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReplaysRepo @Inject constructor(
    private val miscDAO: MiscDAO,
    private val categoryAmountFormulasConverter: CategoryAmountFormulasConverter,
    private val categoriesInteractor: CategoriesInteractor,
) {
    val replays = fetchReplays().asFlow().stateIn(GlobalScope, SharingStarted.Eagerly, listOf())

    fun add(basicReplay: BasicReplay): Completable =
        miscDAO.push(basicReplay.toDTO(categoryAmountFormulasConverter)).subscribeOn(Schedulers.io())

    fun delete(basicReplayName: String): Completable =
        miscDAO.delete(basicReplayName).subscribeOn(Schedulers.io())

    fun fetchReplays(): Observable<List<IReplay>> =
        miscDAO.fetchBasicReplays().subscribeOn(Schedulers.io())
            .map { it.map { BasicReplay.fromDTO(it, categoryAmountFormulasConverter, categoriesInteractor) } }

    fun update(basicReplay: BasicReplay): Completable =
        miscDAO.update(basicReplay.toDTO(categoryAmountFormulasConverter)).subscribeOn(Schedulers.io())
}