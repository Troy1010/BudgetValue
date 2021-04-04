package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.budgetvalue.extensions.launch
import com.tminus1010.budgetvalue.transactions.data.ITransactionsRepo
import com.tminus1010.budgetvalue.transactions.domain.CategorizeTransactionsDomain
import com.tminus1010.tmcommonkotlin.rx.extensions.toBehaviorSubject
import com.tminus1010.tmcommonkotlin.rx.extensions.unbox
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CategorizeTransactionsAdvancedVM @Inject constructor(
    transactionsRepo: ITransactionsRepo,
    categorizeTransactionsDomain: CategorizeTransactionsDomain,
) : ViewModel() {
    // # User Intent Buses
    val intentRememberCA = PublishSubject.create<Pair<Category, BigDecimal>>()
    // # Databindable State
    val transactionToPush = categorizeTransactionsDomain.transactionBox
        .unbox()
        .switchMap {
            intentRememberCA
                .scan(it) { acc, v ->
                    acc.copy(categoryAmounts = acc.categoryAmounts.toMutableMap().also { it[v.first] = -v.second })
                }
        }
        .toBehaviorSubject()
    val defaultAmount = transactionToPush
        .map { it.defaultAmount }
        .toBehaviorSubject()
    // # User Intent Buses
    val intentPushActiveCategories = PublishSubject.create<Unit>()
        .also {
            it
                .withLatestFrom(transactionToPush) { _, b -> b }
                .launch { transactionsRepo.pushTransactionCAs(it, it.categoryAmounts) }
        }
    // # User Intents
    fun rememberCA(category: Category, amount: BigDecimal) {
        intentRememberCA.onNext(Pair(category, amount))
    }
    fun pushRememberedCategories() {
        intentPushActiveCategories.onNext(Unit)
    }
}