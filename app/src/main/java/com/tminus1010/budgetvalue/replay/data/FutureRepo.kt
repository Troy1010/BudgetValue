package com.tminus1010.budgetvalue.replay.data

import com.tminus1010.budgetvalue._core.data.MiscDAO
import com.tminus1010.budgetvalue.categories.CategoryAmountFormulasConverter
import com.tminus1010.budgetvalue.categories.ICategoryParser
import com.tminus1010.budgetvalue.replay.models.BasicFuture
import com.tminus1010.budgetvalue.replay.models.IFuture
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class FutureRepo @Inject constructor(
    private val miscDAO: MiscDAO,
    private val categoryAmountFormulasConverter: CategoryAmountFormulasConverter,
    private val categoryParser: ICategoryParser,
) {
    fun add(basicFuture: BasicFuture): Completable =
        miscDAO.add(basicFuture.toDTO(categoryAmountFormulasConverter)).subscribeOn(Schedulers.io())

    fun delete(basicFutureName: String): Completable =
        miscDAO.delete(basicFutureName).subscribeOn(Schedulers.io())

    fun fetchFutures(): Observable<List<IFuture>> =
        miscDAO.fetchBasicFutures().subscribeOn(Schedulers.io())
            .map { it.map { BasicFuture.fromDTO(it, categoryAmountFormulasConverter, categoryParser) } }
}