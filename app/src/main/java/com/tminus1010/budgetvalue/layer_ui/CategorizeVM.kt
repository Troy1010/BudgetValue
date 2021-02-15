package com.tminus1010.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.extensions.unbox
import com.tminus1010.budgetvalue.layer_data.Repo
import com.tminus1010.budgetvalue.model_app.Category
import com.tminus1010.budgetvalue.unbox
import com.tminus1010.tmcommonkotlin.tuple.Box
import com.tminus1010.tmcommonkotlin_rx.toBehaviorSubject
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal

class CategorizeVM(val repo: Repo, transactionsVM: TransactionsVM): ViewModel() {
    val transactionBox = transactionsVM.uncategorizedSpends
        .map { Box(it.getOrNull(0)) }
    fun finishTransactionWithCategory(category: Category) {
        transactionBox
            .observeOn(Schedulers.io())
            .take(1)
            .unbox()
            .doOnNext { activeCA[category] = it.amount - activeCA.map{ it.value }.fold(0.toBigDecimal()) { acc, v -> acc + v } }
            .flatMapCompletable { repo.updateTransactionCategoryAmounts(it.id, activeCA.mapKeys { it.key.name }) }
            .doOnComplete { activeCA.clear() }
            .subscribe()
    }
    var activeCA = mutableMapOf<Category, BigDecimal>()
    val hasUncategorizedTransaction = transactionBox
        .map { it.unbox != null }
}