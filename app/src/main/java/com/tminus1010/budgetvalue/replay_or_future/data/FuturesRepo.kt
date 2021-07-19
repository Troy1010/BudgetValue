package com.tminus1010.budgetvalue.replay_or_future.data

import com.tminus1010.budgetvalue._core.data.MiscDAO
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue.categories.CategoryAmountFormulasConverter
import com.tminus1010.budgetvalue.categories.ICategoryParser
import com.tminus1010.budgetvalue.replay_or_future.models.BasicFuture
import com.tminus1010.budgetvalue.replay_or_future.models.IFuture
import com.tminus1010.budgetvalue.replay_or_future.models.TotalFuture
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class FuturesRepo @Inject constructor(
    private val miscDAO: MiscDAO,
    private val categoryAmountFormulasConverter: CategoryAmountFormulasConverter,
    private val categoryParser: ICategoryParser,
) {
    fun add(future: IFuture): Completable =
        when(future) {
            is BasicFuture -> miscDAO.add(future.toDTO(categoryAmountFormulasConverter))
            is TotalFuture -> miscDAO.add(future.toDTO(categoryAmountFormulasConverter))
            else -> error("unhandled IFuture")
        }.subscribeOn(Schedulers.io())

    fun delete(future: IFuture): Completable =
        when(future) {
            is BasicFuture -> miscDAO.deleteBasicFuture(future.name)
            is TotalFuture -> miscDAO.deleteTotalFuture(future.name)
            else -> error("unhandled IFuture")
        }.subscribeOn(Schedulers.io())

    fun fetchFutures(): Observable<List<IFuture>> =
        Rx.combineLatest(
            miscDAO.fetchBasicFutures().subscribeOn(Schedulers.io())
                .map { it.map { BasicFuture.fromDTO(it, categoryAmountFormulasConverter, categoryParser) } },
            miscDAO.fetchTotalFutures().subscribeOn(Schedulers.io())
                .map { it.map { TotalFuture.fromDTO(it, categoryAmountFormulasConverter, categoryParser) } },
        ).subscribeOn(Schedulers.io()).map { it.first + it.second }
}