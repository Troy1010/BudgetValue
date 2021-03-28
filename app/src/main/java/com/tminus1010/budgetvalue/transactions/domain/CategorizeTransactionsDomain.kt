package com.tminus1010.budgetvalue.transactions.domain

import com.tminus1010.budgetvalue._core.middleware.unbox
import com.tminus1010.budgetvalue._layer_facades.DomainFacade
import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.tmcommonkotlin.rx.extensions.unbox
import com.tminus1010.tmcommonkotlin.tuple.Box
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategorizeTransactionsDomain @Inject constructor(
    private val domainFacade: DomainFacade,
    transactionsDomain: TransactionsDomain,
) : ICategorizeTransactionsDomain {
    override val transactionBox = transactionsDomain.uncategorizedSpends
        .map { Box(it.getOrNull(0)) }
    override fun finishTransactionWithCategory(category: Category) {
        transactionBox
            .observeOn(Schedulers.io())
            .take(1)
            .unbox()
            .doOnNext { activeCA[category] = it.amount - activeCA.map{ it.value }.fold(0.toBigDecimal()) { acc, v -> acc + v } }
            .flatMapCompletable { domainFacade.pushTransactionCAs(it, activeCA) }
            .doOnComplete { activeCA.clear() }
            .subscribe()
    }
    override var activeCA = mutableMapOf<Category, BigDecimal>()
    override val hasUncategorizedTransaction = transactionBox
        .map { it.unbox != null }
}