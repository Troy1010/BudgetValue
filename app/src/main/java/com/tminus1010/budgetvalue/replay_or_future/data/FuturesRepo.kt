package com.tminus1010.budgetvalue.replay_or_future.data

import com.tminus1010.budgetvalue.all_features.data.MiscDAO
import com.tminus1010.budgetvalue.replay_or_future.domain.BasicFuture
import com.tminus1010.budgetvalue.replay_or_future.domain.IFuture
import com.tminus1010.budgetvalue.replay_or_future.domain.TerminationStrategy
import com.tminus1010.budgetvalue.replay_or_future.domain.TotalFuture
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class FuturesRepo @Inject constructor(
    private val miscDAO: MiscDAO,
) {
    fun add(future: IFuture): Completable {
        return when (future) {
            is BasicFuture -> miscDAO.push(future)
            is TotalFuture -> miscDAO.push(future)
            else -> error("unhandled IFuture")
        }.subscribeOn(Schedulers.io())
    }

    fun setTerminationStatus(future: IFuture, terminationStrategy: TerminationStrategy): Completable {
        return when (future) {
            is BasicFuture -> miscDAO.update(future.copy(terminationStrategy = terminationStrategy))
            is TotalFuture -> miscDAO.update(future.copy(terminationStrategy = terminationStrategy))
            else -> error("unhandled IFuture")
        }.subscribeOn(Schedulers.io())
    }

    fun delete(future: IFuture): Completable =
        when (future) {
            is BasicFuture -> miscDAO.deleteBasicFuture(future.name)
            is TotalFuture -> miscDAO.deleteTotalFuture(future.name)
            else -> error("unhandled IFuture")
        }.subscribeOn(Schedulers.io())

    fun fetchFutures(): Observable<List<IFuture>> =
        Observable.combineLatest(
            miscDAO.fetchBasicFutures().subscribeOn(Schedulers.io()),
            miscDAO.fetchTotalFutures().subscribeOn(Schedulers.io()),
        ) { basicFutures, totalFutures -> basicFutures + totalFutures }
            .subscribeOn(Schedulers.io())
}