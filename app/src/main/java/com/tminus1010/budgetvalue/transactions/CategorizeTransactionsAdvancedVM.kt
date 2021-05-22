package com.tminus1010.budgetvalue.transactions

import com.tminus1010.budgetvalue._core.BaseViewModel
import com.tminus1010.budgetvalue._core.extensions.nonLazyCache
import com.tminus1010.budgetvalue._core.extensions.toLiveData
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.data.ITransactionsRepo
import com.tminus1010.budgetvalue.transactions.domain.CategorizeTransactionsDomain
import com.tminus1010.tmcommonkotlin.rx.extensions.launch
import com.tminus1010.tmcommonkotlin.rx.extensions.unbox
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CategorizeTransactionsAdvancedVM @Inject constructor(
    errorSubject: Subject<Throwable>,
    private val transactionsRepo: ITransactionsRepo,
    categorizeTransactionsDomain: CategorizeTransactionsDomain,
) : BaseViewModel() {
    // # Private
    private val intentRememberCA = PublishSubject.create<Pair<Category, BigDecimal>>()
    // # State
    val transactionToPush = categorizeTransactionsDomain.transactionBox
        .unbox()
        .switchMap {
            intentRememberCA
                .scan(it) { acc, v ->
                    acc.copy(categoryAmounts = acc.categoryAmounts.toMutableMap().also { it[v.first] = -v.second })
                }
        }
        .nonLazyCache(disposables)
    val defaultAmount = transactionToPush
        .map { it.defaultAmount.toString() }
        .toLiveData(errorSubject)
    // # User Intents
    fun rememberCA(category: Category, amount: BigDecimal) {
        intentRememberCA.onNext(Pair(category, amount))
    }
    fun pushRememberedCategories() {
        transactionToPush.take(1)
            .flatMapCompletable { transactionsRepo.pushTransactionCAs(it, it.categoryAmounts) }
            .launch()
    }
}