package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.extensions.divertErrors
import com.tminus1010.budgetvalue._core.extensions.nonLazyCache
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
) : ViewModel() {
    // # Private
    private val intents = PublishSubject.create<Intents>()
    private sealed class Intents {
        object Clear: Intents()
        class Add(val category: Category, val amount: BigDecimal): Intents()
    }
    // # State
    val transactionToPush = categorizeTransactionsDomain.transactionBox
        .unbox()
        .switchMap {
            intents
                .scan(it) { acc, v ->
                    when (v) {
                        is Intents.Clear -> acc.copy(categoryAmounts = emptyMap())
                        is Intents.Add -> acc.copy(categoryAmounts = acc.categoryAmounts.toMutableMap().also { it[v.category] = v.amount })
                    }
                }
        }
        .nonLazyCache(disposables)
    val defaultAmount = transactionToPush
        .map { it.defaultAmount.toString() }
        .divertErrors(errorSubject)
    // # User Intents
    fun rememberCA(category: Category, amount: BigDecimal) {
        intents.onNext(Intents.Add(category, amount))
    }
    fun clearCA() {
        intents.onNext(Intents.Clear)
    }
    fun pushRememberedCategories() {
        transactionToPush.take(1)
            .flatMapCompletable { transactionsRepo.pushTransactionCAs(it, it.categoryAmounts) }
            .launch()
    }
    //
    fun setup(categoryAmounts: Map<Category, BigDecimal>) {
        clearCA()
        categoryAmounts.forEach { rememberCA(it.key, it.value) }
    }
}