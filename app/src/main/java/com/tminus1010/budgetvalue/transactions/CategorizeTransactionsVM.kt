package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tminus1010.budgetvalue._core.App
import com.tminus1010.budgetvalue._layer_facades.DomainFacade
import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.budgetvalue._core.middleware.unbox
import com.tminus1010.tmcommonkotlin.rx.extensions.unbox
import com.tminus1010.tmcommonkotlin.tuple.Box
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CategorizeTransactionsVM @Inject constructor(
    private val domainFacade: DomainFacade,
    transactionsVM: TransactionsVM,
): ViewModel() {
    val transactionBox = transactionsVM.uncategorizedSpends
        .map { Box(it.getOrNull(0)) }
    fun finishTransactionWithCategory(category: Category) {
        transactionBox
            .observeOn(Schedulers.io())
            .take(1)
            .unbox()
            .doOnNext { activeCA[category] = it.amount - activeCA.map{ it.value }.fold(0.toBigDecimal()) { acc, v -> acc + v } }
            .flatMapCompletable { domainFacade.pushTransactionCAs(it, activeCA) }
            .doOnComplete { activeCA.clear() }
            .subscribe()
    }
    var activeCA = mutableMapOf<Category, BigDecimal>()
    val hasUncategorizedTransaction = transactionBox
        .map { it.unbox != null }
}