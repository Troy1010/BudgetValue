package com.tminus1010.budgetvalue.layer_ui

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.extensions.launch
import com.tminus1010.budgetvalue.layer_domain.Domain
import com.tminus1010.budgetvalue.model_domain.Category
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import com.tminus1010.tmcommonkotlin.rx.extensions.unbox
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal

class CategorizeAdvancedVM(
    domain: Domain,
    categorizeTransactionsVM: CategorizeTransactionsVM,
) : ViewModel() {
    val intentRememberCA = PublishSubject.create<Pair<Category, BigDecimal>>()
    val transactionToPush = categorizeTransactionsVM.transactionBox
        .unbox()
        .switchMap {
            intentRememberCA
                .scan(it) { acc, v ->
                    acc.copy(categoryAmounts = acc.categoryAmounts.toMutableMap().also { it[v.first] = -v.second })
                }
        }
        .toBehaviorSubject()
    val intentPushActiveCategories = PublishSubject.create<Unit>()
        .also {
            it
                .withLatestFrom(transactionToPush) { _, b -> b }
                .launch { domain.pushTransactionCAs(it, it.categoryAmounts) }
        }
    val defaultAmount = transactionToPush
        .map { it.defaultAmount }
        .toBehaviorSubject()
}